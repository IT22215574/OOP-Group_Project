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
    login:           (email, password) => api.request('POST', '/auth/login',           { email, password }),
    register:        (data)            => api.request('POST', '/auth/register',          data),
    registerAgent:   (data)            => api.request('POST', '/auth/register-agent',    data),
    logout:          ()                => api.request('POST', '/auth/logout'),
    getMe:           ()                => api.request('GET',  '/auth/me'),

    // Properties
    getProperties: (params = {}) => {
        const qs = new URLSearchParams(params).toString();
        return api.request('GET', `/properties${qs ? '?' + qs : ''}`);
    },
    getProperty:     (id)       => api.request('GET',    `/properties/${id}`),
    getMyProperties: (agentId) => api.request('GET',    `/properties?agentId=${agentId}`),
    createProperty:  (data)    => api.request('POST',   '/properties', data),
    updateProperty:  (id, data)=> api.request('PUT',    `/properties/${id}`, data),
    deleteProperty:  (id)      => api.request('DELETE', `/properties/${id}`),

    // Users (admin)
    getUsers: (params = {}) => {
        const qs = new URLSearchParams(params).toString();
        return api.request('GET', `/users${qs ? '?' + qs : ''}`);
    },
    getUser:    (id)       => api.request('GET',    `/users/${id}`),
    updateUser: (id, data) => api.request('PUT',    `/users/${id}`, data),
    deleteUser: (id)       => api.request('DELETE', `/users/${id}`),

    // Agents
    getAgents: (params = {}) => {
        const qs = new URLSearchParams(params).toString();
        return api.request('GET', `/agents${qs ? '?' + qs : ''}`);
    },
    getAgent:    (id)       => api.request('GET',    `/agents/${id}`),
    getAgentByUserId: (userId) => api.request('GET', `/agents?userId=${userId}`),
    createAgent: (data)     => api.request('POST',   '/agents', data),
    updateAgent: (id, data) => api.request('PUT',    `/agents/${id}`, data),
    deleteAgent: (id)       => api.request('DELETE', `/agents/${id}`),

    // Advertisements
    getAds: (params = {}) => {
        const qs = new URLSearchParams(params).toString();
        return api.request('GET', `/advertisements${qs ? '?' + qs : ''}`);
    },
    getAd:    (id)       => api.request('GET',    `/advertisements/${id}`),
    deleteAd: (id)       => api.request('DELETE', `/advertisements/${id}`),

    // Appointments
    scheduleVisit:     (data)        => api.request('POST',   '/appointments', data),
    getAppointments:   (params = {}) => {
        const qs = new URLSearchParams(params).toString();
        return api.request('GET', `/appointments${qs ? '?' + qs : ''}`);
    },
    updateAppointment: (id, data)    => api.request('PUT',    `/appointments/${id}`, data),
    cancelAppointment: (id)          => api.request('DELETE', `/appointments/${id}`),

    // Image upload (multipart — no JSON Content-Type)
    uploadImage: async (formData) => {
        const res = await fetch(`${API_BASE}/upload`, {
            method: 'POST',
            credentials: 'include',
            body: formData
        });
        return res.json();
    },
};
