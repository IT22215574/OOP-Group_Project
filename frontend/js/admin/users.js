async function requireAdmin() {
    try {
        const res = await api.getMe();
        if (!res.success || res.data.role !== 'admin') window.location.href = '../../pages/login.html';
        document.getElementById('admin-name').textContent = res.data.fullName;
    } catch (_) { window.location.href = '../../pages/login.html'; }
}

async function loadUsers() {
    const name  = document.getElementById('filter-name').value.trim();
    const email = document.getElementById('filter-email').value.trim();
    const role  = document.getElementById('filter-role').value;

    const params = {};
    if (name)  params.name  = name;
    if (email) params.email = email;

    const tbody = document.getElementById('users-tbody');
    tbody.innerHTML = '<tr><td colspan="7" class="px-6 py-10 text-center text-gray-400">Loading...</td></tr>';

    try {
        const res = await api.getUsers(params);
        if (!res.success) throw new Error(res.message);
        let users = res.data;
        if (role) users = users.filter(u => u.role === role);
        renderUsers(users);
    } catch (e) {
        tbody.innerHTML = `<tr><td colspan="7" class="px-6 py-10 text-center text-red-400">${e.message}</td></tr>`;
    }
}

function renderUsers(users) {
    const tbody = document.getElementById('users-tbody');
    if (!users.length) {
        tbody.innerHTML = '<tr><td colspan="7" class="px-6 py-10 text-center text-gray-400">No users found</td></tr>';
        return;
    }
    tbody.innerHTML = users.map(u => `
        <tr class="hover:bg-gray-50">
            <td class="px-6 py-3 text-gray-400 text-xs">#${u.id}</td>
            <td class="px-6 py-3 font-medium text-gray-800">${u.fullName}</td>
            <td class="px-6 py-3 text-gray-500">${u.email}</td>
            <td class="px-6 py-3 text-gray-500">${u.phone || '—'}</td>
            <td class="px-6 py-3">${roleBadge(u.role)}</td>
            <td class="px-6 py-3">${statusBadge(u.accountStatus)}</td>
            <td class="px-6 py-3">
                <div class="flex gap-2">
                    <button onclick='openEdit(${JSON.stringify(u)})'
                            class="text-xs bg-primary text-white px-3 py-1.5 rounded-lg hover:bg-primary-dark transition">Edit</button>
                    <button onclick="deleteUser(${u.id}, '${u.fullName}')"
                            class="text-xs bg-red-50 text-red-600 border border-red-200 px-3 py-1.5 rounded-lg hover:bg-red-100 transition">Delete</button>
                </div>
            </td>
        </tr>`).join('');
}

function openEdit(user) {
    document.getElementById('edit-id').value       = user.id;
    document.getElementById('edit-fullName').value = user.fullName;
    document.getElementById('edit-email').value    = user.email;
    document.getElementById('edit-phone').value    = user.phone || '';
    document.getElementById('edit-role').value     = user.role;
    document.getElementById('edit-status').value   = user.accountStatus;
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
        fullName:      document.getElementById('edit-fullName').value.trim(),
        email:         document.getElementById('edit-email').value.trim(),
        phone:         document.getElementById('edit-phone').value.trim(),
        role:          document.getElementById('edit-role').value,
        accountStatus: document.getElementById('edit-status').value,
    };
    try {
        const res = await api.updateUser(id, data);
        if (res.success) {
            closeModal();
            loadUsers();
        } else {
            showModalAlert(res.message || 'Update failed', 'error');
        }
    } catch (_) {
        showModalAlert('Connection error', 'error');
    }
});

async function deleteUser(id, name) {
    if (!confirm(`Delete user "${name}"? This cannot be undone.`)) return;
    try {
        const res = await api.deleteUser(id);
        if (res.success) loadUsers();
        else alert(res.message || 'Delete failed');
    } catch (_) { alert('Connection error'); }
}

function resetFilters() {
    document.getElementById('filter-name').value  = '';
    document.getElementById('filter-email').value = '';
    document.getElementById('filter-role').value  = '';
    loadUsers();
}

function showModalAlert(msg, type) {
    const el = document.getElementById('modal-alert');
    el.textContent = msg;
    el.className = `mb-4 p-3 rounded-lg text-sm ${type === 'error' ? 'bg-red-50 text-red-700 border border-red-200' : 'bg-green-50 text-green-700 border border-green-200'}`;
    el.classList.remove('hidden');
}

function roleBadge(role) {
    const map = { admin: 'bg-red-100 text-red-700', agent: 'bg-blue-100 text-blue-700', buyer: 'bg-gray-100 text-gray-600' };
    return `<span class="px-2 py-0.5 rounded-full text-xs font-medium ${map[role] || 'bg-gray-100 text-gray-600'}">${role}</span>`;
}

function statusBadge(status) {
    const map = { active: 'bg-green-100 text-green-700', inactive: 'bg-gray-100 text-gray-500', suspended: 'bg-red-100 text-red-700' };
    return `<span class="px-2 py-0.5 rounded-full text-xs font-medium ${map[status] || 'bg-gray-100 text-gray-500'}">${status || '—'}</span>`;
}

function adminLogout() {
    api.logout().catch(() => {});
    sessionStorage.removeItem('user');
    window.location.href = '../../pages/login.html';
}

requireAdmin().then(loadUsers);
