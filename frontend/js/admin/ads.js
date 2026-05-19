async function requireAdmin() {
    try {
        const res = await api.getMe();
        if (!res.success || res.data.role !== 'admin') window.location.href = '../../pages/login.html';
        document.getElementById('admin-name').textContent = res.data.fullName;
    } catch (_) { window.location.href = '../../pages/login.html'; }
}

async function loadAds() {
    const title    = document.getElementById('filter-title').value.trim();
    const location = document.getElementById('filter-location').value.trim();
    const type     = document.getElementById('filter-type').value;
    const status   = document.getElementById('filter-status').value;

    const params = {};
    if (title)    params.title        = title;
    if (location) params.location     = location;
    if (type)     params.propertyType = type;
    if (status)   params.status       = status;

    const tbody = document.getElementById('ads-tbody');
    tbody.innerHTML = '<tr><td colspan="8" class="px-6 py-10 text-center text-gray-400">Loading...</td></tr>';

    try {
        const res = await api.getAds(params);
        if (!res.success) throw new Error(res.message);
        renderAds(res.data);
    } catch (e) {
        tbody.innerHTML = `<tr><td colspan="8" class="px-6 py-10 text-center text-red-400">${e.message}</td></tr>`;
    }
}

function renderAds(ads) {
    const tbody = document.getElementById('ads-tbody');
    if (!ads.length) {
        tbody.innerHTML = '<tr><td colspan="8" class="px-6 py-10 text-center text-gray-400">No advertisements found</td></tr>';
        return;
    }
    tbody.innerHTML = ads.map(a => `
        <tr class="hover:bg-gray-50">
            <td class="px-6 py-3 text-gray-400 text-xs">#${a.id}</td>
            <td class="px-6 py-3 font-medium text-gray-800 max-w-[200px] truncate">${a.propertyTitle}</td>
            <td class="px-6 py-3 text-gray-500 capitalize">${a.propertyType || '—'}</td>
            <td class="px-6 py-3 text-gray-500">${a.location || '—'}</td>
            <td class="px-6 py-3 font-medium text-gray-800">LKR ${Number(a.price).toLocaleString()}</td>
            <td class="px-6 py-3 text-gray-500">${a.agentName || '—'}</td>
            <td class="px-6 py-3">${adStatusBadge(a.availabilityStatus)}</td>
            <td class="px-6 py-3">
                <button onclick="deleteAd(${a.id}, '${(a.propertyTitle || '').replace(/'/g,'')}')"
                        class="text-xs bg-red-50 text-red-600 border border-red-200 px-3 py-1.5 rounded-lg hover:bg-red-100 transition">Delete</button>
            </td>
        </tr>`).join('');
}

async function deleteAd(id, title) {
    if (!confirm(`Delete advertisement "${title}"? This cannot be undone.`)) return;
    try {
        const res = await api.deleteAd(id);
        if (res.success) loadAds();
        else alert(res.message || 'Delete failed');
    } catch (_) { alert('Connection error'); }
}

function resetFilters() {
    document.getElementById('filter-title').value    = '';
    document.getElementById('filter-location').value = '';
    document.getElementById('filter-type').value     = '';
    document.getElementById('filter-status').value   = '';
    loadAds();
}

function adStatusBadge(status) {
    const map = { available: 'bg-green-100 text-green-700', sold: 'bg-gray-100 text-gray-600', rented: 'bg-yellow-100 text-yellow-700' };
    return `<span class="px-2 py-0.5 rounded-full text-xs font-medium ${map[status] || 'bg-gray-100 text-gray-500'} capitalize">${status || '—'}</span>`;
}

function adminLogout() {
    api.logout().catch(() => {});
    sessionStorage.removeItem('user');
    window.location.href = '../../pages/login.html';
}

requireAdmin().then(loadAds);
