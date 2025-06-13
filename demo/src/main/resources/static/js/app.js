// Variables globals
let currentFilter = 'all';
let tasks = [];
const API_BASE = '/api/tasks';

// Inicialització
document.addEventListener('DOMContentLoaded', function () {
    loadStats();
    loadTasks();
    setupEventListeners();

    // Actualització automàtica cada 30 segons
    setInterval(() => {
        loadStats();
        loadTasks();
    }, 30000);
});

// Configurar listeners d'esdeveniments
function setupEventListeners() {
    // Formulari de nova tasca
    document.getElementById('taskForm').addEventListener('submit', createTask);

    // Formulari d'edició
    document.getElementById('editForm').addEventListener('submit', updateTask);

    // Filtres
    document.querySelectorAll('.filter-btn').forEach(btn => {
        btn.addEventListener('click', function () {
            document.querySelectorAll('.filter-btn').forEach(b => b.classList.remove('active'));
            this.classList.add('active');
            currentFilter = this.dataset.filter;
            loadTasks();
        });
    });

    // Cerca
    document.getElementById('searchInput').addEventListener('input', handleSearch);

    // Modal
    document.querySelector('.close').addEventListener('click', closeModal);
    window.addEventListener('click', function (event) {
        const modal = document.getElementById('editModal');
        if (event.target == modal) {
            closeModal();
        }
    });
}

// Carregar estadístiques
async function loadStats() {
    try {
        const response = await fetch(`${API_BASE}/stats`);
        const stats = await response.json();

        document.getElementById('totalTasks').textContent = stats.total;
        document.getElementById('completedTasks').textContent = stats.completed;
        document.getElementById('pendingTasks').textContent = stats.pending;
        document.getElementById('urgentTasks').textContent = stats.urgent;

        const percentage = stats.completionPercentage.toFixed(1);
        document.getElementById('progressBar').style.width = `${percentage}%`;
        document.getElementById('progressText').textContent = `${percentage}%`;
    } catch (error) {
        console.error('Error carregant estadístiques:', error);
    }
}

// Carregar tasques
async function loadTasks() {
    try {
        let url = API_BASE;

        switch (currentFilter) {
            case 'pending':
                url += '/pending';
                break;
            case 'completed':
                url += '/completed';
                break;
            case 'urgent':
                url += '/urgent';
                break;
            case 'today':
                url += '/today';
                break;
        }

        const response = await fetch(url);
        tasks = await response.json();
        renderTasks(tasks);
    } catch (error) {
        console.error('Error carregant tasques:', error);
        showNotification('Error carregant tasques', 'error');
    }
}

// Renderitzar tasques
function renderTasks(tasksToRender) {
    const container = document.getElementById('tasksContainer');

    if (tasksToRender.length === 0) {
        container.innerHTML = '<div class="loading">📝 No hi ha tasques per mostrar</div>';
        return;
    }

    container.innerHTML = tasksToRender.map(task => `
        <div class="task-item ${task.completed ? 'completed' : ''}" onclick="editTask(${task.id})">
            <div class="task-header">
                <div class="task-description ${task.completed ? 'completed' : ''}">
                    ${task.description}
                </div>
                <span class="task-priority priority-${task.priority.toLowerCase()}">
                    ${getPriorityText(task.priority)}
                </span>
            </div>
            <div class="task-meta">
                <span>Creada: ${formatDate(task.createdAt)}</span>
                ${task.completedAt ? `<span>Completada: ${formatDate(task.completedAt)}</span>` : ''}
            </div>
            ${task.notes ? `<div style="margin-bottom: 15px; color: #718096;">${task.notes}</div>` : ''}
            <div class="task-actions" onclick="event.stopPropagation()">
                ${task.completed ?
            `<button class="btn btn-warning btn-small" onclick="toggleTask(${task.id}, false)">
                        ↩️ Marcar Pendent
                    </button>` :
            `<button class="btn btn-success btn-small" onclick="toggleTask(${task.id}, true)">
                        ✅ Completar
                    </button>`
        }
                <button class="btn btn-danger btn-small" onclick="deleteTask(${task.id})">
                    🗑️ Eliminar
                </button>
            </div>
        </div>
    `).join('');
}

// Crear nova tasca
async function createTask(event) {
    event.preventDefault();

    const formData = new FormData(event.target);
    const taskData = {
        description: formData.get('description'),
        priority: formData.get('priority'),
        notes: formData.get('notes') || null
    };

    try {
        const response = await fetch(API_BASE, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(taskData)
        });

        if (response.ok) {
            event.target.reset();
            loadStats();
            loadTasks();
            showNotification('Tasca creada correctament', 'success');
        } else {
            showNotification('Error creant la tasca', 'error');
        }
    } catch (error) {
        console.error('Error creant tasca:', error);
        showNotification('Error creant la tasca', 'error');
    }
}

// Canviar estat de tasca
async function toggleTask(taskId, completed) {
    try {
        const endpoint = completed ? 'complete' : 'uncomplete';
        const response = await fetch(`${API_BASE}/${taskId}/${endpoint}`, {
            method: 'PATCH'
        });

        if (response.ok) {
            loadStats();
            loadTasks();
            showNotification(`Tasca ${completed ? 'completada' : 'marcada com a pendent'}`, 'success');
        } else {
            showNotification('Error actualitzant la tasca', 'error');
        }
    } catch (error) {
        console.error('Error actualitzant tasca:', error);
        showNotification('Error actualitzant la tasca', 'error');
    }
}

// Eliminar tasca
async function deleteTask(taskId) {
    if (!confirm('Estàs segur que vols eliminar aquesta tasca?')) {
        return;
    }

    try {
        const response = await fetch(`${API_BASE}/${taskId}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            loadStats();
            loadTasks();
            showNotification('Tasca eliminada correctament', 'success');
        } else {
            showNotification('Error eliminant la tasca', 'error');
        }
    } catch (error) {
        console.error('Error eliminant tasca:', error);
        showNotification('Error eliminant la tasca', 'error');
    }
}

// Editar tasca
function editTask(taskId) {
    const task = tasks.find(t => t.id === taskId);
    if (!task) return;

    document.getElementById('editTaskId').value = task.id;
    document.getElementById('editDescription').value = task.description;
    document.getElementById('editPriority').value = task.priority;
    document.getElementById('editNotes').value = task.notes || '';

    document.getElementById('editModal').style.display = 'block';
}

// Actualitzar tasca
async function updateTask(event) {
    event.preventDefault();

    const taskId = document.getElementById('editTaskId').value;
    const formData = new FormData(event.target);
    const taskData = {
        description: formData.get('description'),
        priority: formData.get('priority'),
        notes: formData.get('notes') || null
    };

    try {
        const response = await fetch(`${API_BASE}/${taskId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(taskData)
        });

        if (response.ok) {
            closeModal();
            loadStats();
            loadTasks();
            showNotification('Tasca actualitzada correctament', 'success');
        } else {
            showNotification('Error actualitzant la tasca', 'error');
        }
    } catch (error) {
        console.error('Error actualitzant tasca:', error);
        showNotification('Error actualitzant la tasca', 'error');
    }
}

// Cerca de tasques
async function handleSearch(event) {
    const searchTerm = event.target.value.trim();

    if (searchTerm === '') {
        loadTasks();
        return;
    }

    try {
        const response = await fetch(`${API_BASE}/search?q=${encodeURIComponent(searchTerm)}`);
        const searchResults = await response.json();
        renderTasks(searchResults);
    } catch (error) {
        console.error('Error cercant tasques:', error);
        showNotification('Error cercant tasques', 'error');
    }
}

// Accions ràpides
async function markAllCompleted() {
    if (!confirm('Estàs segur que vols marcar totes les tasques com a completades?')) {
        return;
    }

    try {
        const response = await fetch(`${API_BASE}/complete-all`, {
            method: 'PATCH'
        });

        if (response.ok) {
            loadStats();
            loadTasks();
            showNotification('Totes les tasques marcades com a completades', 'success');
        } else {
            showNotification('Error completant les tasques', 'error');
        }
    } catch (error) {
        console.error('Error completant tasques:', error);
        showNotification('Error completant les tasques', 'error');
    }
}

async function deleteCompletedTasks() {
    if (!confirm('Estàs segur que vols eliminar totes les tasques completades?')) {
        return;
    }

    try {
        const response = await fetch(`${API_BASE}/completed`, {
            method: 'DELETE'
        });

        if (response.ok) {
            loadStats();
            loadTasks();
            showNotification('Tasques completades eliminades correctament', 'success');
        } else {
            showNotification('Error eliminant les tasques completades', 'error');
        }
    } catch (error) {
        console.error('Error eliminant tasques completades:', error);
        showNotification('Error eliminant les tasques completades', 'error');
    }
}

function refreshTasks() {
    loadStats();
    loadTasks();
    showNotification('Tasques actualitzades', 'success');
}

// Utilitats
function closeModal() {
    document.getElementById('editModal').style.display = 'none';
}

function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('ca-ES', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

function getPriorityText(priority) {
    const priorities = {
        'LOW': 'Baixa',
        'MEDIUM': 'Mitjana',
        'HIGH': 'Alta',
        'URGENT': 'Urgent'
    };
    return priorities[priority] || priority;
}

function showNotification(message, type) {
    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    notification.textContent = message;

    document.body.appendChild(notification);

    setTimeout(() => {
        notification.remove();
    }, 3000);
} 