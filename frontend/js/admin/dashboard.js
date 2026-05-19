async function requireAdmin() {
    try {
        const res = await api.getMe();
        if (!res.success || res.data.role !== 'admin') {
            window.location.href = '../../pages/login.html';
        }
        document.getElementById('admin-name').textContent = res.data.fullName;
    } catch (_) {
        window.location.href = '../../pages/login.html';
    }
}

async function loadStats() {
    const [usersRes, agentsRes, propsRes, adsRes] = await Promise.allSettled([
        api.getUsers(),
        api.getAgents(),
        api.getProperties({ limit: 1000 }),
        api.getAds()
    ]);

    if (usersRes.status === 'fulfilled' && usersRes.value.success) {
        document.getElementById('stat-users').textContent = usersRes.value.data.length;
        renderRecentUsers(usersRes.value.data.slice(0, 5));
    }
    if (agentsRes.status === 'fulfilled' && agentsRes.value.success) {
        document.getElementById('stat-agents').textContent = agentsRes.value.data.length;
    }
    if (propsRes.status === 'fulfilled' && propsRes.value.success) {
        document.getElementById('stat-properties').textContent = propsRes.value.data.total ?? propsRes.value.data.properties?.length ?? '—';
    }
    if (adsRes.status === 'fulfilled' && adsRes.value.success) {
        document.getElementById('stat-ads').textContent = adsRes.value.data.length;
    }
}

function renderRecentUsers(users) {
    const tbody = document.getElementById('recent-users');
    if (!users.length) {
        tbody.innerHTML = '<tr><td colspan="4" class="px-6 py-8 text-center text-gray-400">No users found</td></tr>';
        return;
    }
    tbody.innerHTML = users.map(u => `
        <tr class="hover:bg-gray-50">
            <td class="px-6 py-3 font-medium text-gray-800">${u.fullName}</td>
            <td class="px-6 py-3 text-gray-500">${u.email}</td>
            <td class="px-6 py-3">${roleBadge(u.role)}</td>
            <td class="px-6 py-3">${statusBadge(u.accountStatus)}</td>
        </tr>`).join('');
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

requireAdmin().then(loadStats);
