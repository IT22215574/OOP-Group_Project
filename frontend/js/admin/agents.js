async function requireAdmin() {
    try {
        const res = await api.getMe();
        if (!res.success || res.data.role !== 'admin') window.location.href = '../../pages/login.html';
        document.getElementById('admin-name').textContent = res.data.fullName;
    } catch (_) { window.location.href = '../../pages/login.html'; }
}

async function loadAgents() {
    const name           = document.getElementById('filter-name').value.trim();
    const location       = document.getElementById('filter-location').value.trim();
    const specialization = document.getElementById('filter-specialization').value;

    const params = {};
    if (name)           params.name           = name;
    if (location)       params.location       = location;
    if (specialization) params.specialization = specialization;

    const tbody = document.getElementById('agents-tbody');
    tbody.innerHTML = '<tr><td colspan="9" class="px-6 py-10 text-center text-gray-400">Loading...</td></tr>';

    try {
        const res = await api.getAgents(params);
        if (!res.success) throw new Error(res.message);
        renderAgents(res.data);
    } catch (e) {
        tbody.innerHTML = `<tr><td colspan="9" class="px-6 py-10 text-center text-red-400">${e.message}</td></tr>`;
    }
}

function renderAgents(agents) {
    const tbody = document.getElementById('agents-tbody');
    if (!agents.length) {
        tbody.innerHTML = '<tr><td colspan="9" class="px-6 py-10 text-center text-gray-400">No agents found</td></tr>';
        return;
    }
    tbody.innerHTML = agents.map(a => `
        <tr class="hover:bg-gray-50">
            <td class="px-6 py-3 text-gray-400 text-xs">#${a.id}</td>
            <td class="px-6 py-3 font-medium text-gray-800">${a.fullName}</td>
            <td class="px-6 py-3 text-gray-500">${a.email}</td>
            <td class="px-6 py-3 text-gray-500">${a.contactNumber || '—'}</td>
            <td class="px-6 py-3 text-gray-500">${a.location || '—'}</td>
            <td class="px-6 py-3 text-gray-500">${a.experienceYears} yr</td>
            <td class="px-6 py-3 text-gray-500 capitalize">${a.propertySpecialization || '—'}</td>
            <td class="px-6 py-3">${availBadge(a.availabilityStatus)}</td>
            <td class="px-6 py-3">
                <div class="flex gap-2">
                    <button onclick='openEdit(${JSON.stringify(a)})'
                            class="text-xs bg-primary text-white px-3 py-1.5 rounded-lg hover:bg-primary-dark transition">Edit</button>
                    <button onclick="deleteAgent(${a.id}, '${a.fullName}')"
                            class="text-xs bg-red-50 text-red-600 border border-red-200 px-3 py-1.5 rounded-lg hover:bg-red-100 transition">Delete</button>
                </div>
            </td>
        </tr>`).join('');
}

function openEdit(agent) {
    document.getElementById('edit-id').value             = agent.id;
    document.getElementById('edit-fullName').value       = agent.fullName;
    document.getElementById('edit-email').value          = agent.email;
    document.getElementById('edit-contact').value        = agent.contactNumber || '';
    document.getElementById('edit-location').value       = agent.location || '';
    document.getElementById('edit-experience').value     = agent.experienceYears || 0;
    document.getElementById('edit-specialization').value = agent.propertySpecialization || 'house';
    document.getElementById('edit-status').value         = agent.availabilityStatus || 'available';
    document.getElementById('modal-alert').classList.add('hidden');
    document.getElementById('edit-modal').classList.remove('hidden');
}

function closeModal() {
    document.getElementById('edit-modal').classList.add('hidden');
}

document.getElementById('edit-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const id = document.getElementById('edit-id').value;
    const data = {
        fullName:              document.getElementById('edit-fullName').value.trim(),
        email:                 document.getElementById('edit-email').value.trim(),
        contactNumber:         document.getElementById('edit-contact').value.trim(),
        location:              document.getElementById('edit-location').value.trim(),
        experienceYears:       parseInt(document.getElementById('edit-experience').value) || 0,
        propertySpecialization:document.getElementById('edit-specialization').value,
        availabilityStatus:    document.getElementById('edit-status').value,
    };
    try {
        const res = await api.updateAgent(id, data);
        if (res.success) { closeModal(); loadAgents(); }
        else showModalAlert(res.message || 'Update failed', 'error');
    } catch (_) { showModalAlert('Connection error', 'error'); }
});

async function deleteAgent(id, name) {
    if (!confirm(`Delete agent "${name}"? This cannot be undone.`)) return;
    try {
        const res = await api.deleteAgent(id);
        if (res.success) loadAgents();
        else alert(res.message || 'Delete failed');
    } catch (_) { alert('Connection error'); }
}

function resetFilters() {
    document.getElementById('filter-name').value           = '';
    document.getElementById('filter-location').value       = '';
    document.getElementById('filter-specialization').value = '';
    loadAgents();
}

function showModalAlert(msg, type) {
    const el = document.getElementById('modal-alert');
    el.textContent = msg;
    el.className = `mb-4 p-3 rounded-lg text-sm ${type === 'error' ? 'bg-red-50 text-red-700 border border-red-200' : 'bg-green-50 text-green-700 border border-green-200'}`;
    el.classList.remove('hidden');
}

function availBadge(status) {
    const map = { available: 'bg-green-100 text-green-700', busy: 'bg-yellow-100 text-yellow-700', inactive: 'bg-gray-100 text-gray-500' };
    return `<span class="px-2 py-0.5 rounded-full text-xs font-medium ${map[status] || 'bg-gray-100 text-gray-500'}">${status || '—'}</span>`;
}

function adminLogout() {
    api.logout().catch(() => {});
    sessionStorage.removeItem('user');
    window.location.href = '../../pages/login.html';
}

requireAdmin().then(loadAgents);
