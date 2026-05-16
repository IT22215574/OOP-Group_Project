// Central API client for PrimeEstate backend
const API_BASE = CONFIG.API_BASE;

const api = {
    async request(method, endpoint, body = null) {
        const options = {
            method,
            credentials: 'include',
            headers: { 'Content-Type': 'application/json' }
        };
        if (body) options.body = JSON.stringify(body);

        const res = await fetch(`${API_BASE}${endpoint}`, options);
        return res.json();
    },

    // Auth
    login:    (email, password)  => api.request('POST', '/auth/login',    { email, password }),
    register: (data)             => api.request('POST', '/auth/register',  data),
    logout:   ()                 => api.request('POST', '/auth/logout'),
    getMe:    ()                 => api.request('GET',  '/auth/me'),

    // Properties
    getProperties: (params = {}) => {
        const qs = new URLSearchParams(params).toString();
        return api.request('GET', `/properties${qs ? '?' + qs : ''}`);
    },
    getProperty:   (id)          => api.request('GET',    `/properties/${id}`),
    createProperty:(data)        => api.request('POST',   '/properties', data),
    updateProperty:(id, data)    => api.request('PUT',    `/properties/${id}`, data),
    deleteProperty:(id)          => api.request('DELETE', `/properties/${id}`)
};
