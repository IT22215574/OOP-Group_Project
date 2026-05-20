**Admin Access (Client-side for Assignment)**

- **Purpose:** quick instructions to mark a user as an administrator in this assignment (no server-side roles are persisted).

- **Register an admin (recommended):** Use the admin signup page to create an admin account. The page is at [frontend/pages/register-admin.html](frontend/pages/register-admin.html). It registers a user and sets `isAdmin` in browser storage so the admin UI becomes available.

- **Email-based UI admin:** Set the admin email used for client-side detection in [frontend/js/config.js](frontend/js/config.js). When a user logs in with this email the frontend treats them as admin.

- **Manual localStorage edit (dev):** In the browser DevTools Console, set the logged user to include `isAdmin: true` and re-save to `localStorage` or `sessionStorage`:

  - Example:

    const u = JSON.parse(localStorage.getItem('user')) || {};
    u.isAdmin = true;
    localStorage.setItem('user', JSON.stringify(u));

- **Admin UI and API:** The admin panel page is [frontend/pages/admin-users.html](frontend/pages/admin-users.html) and it calls the backend admin endpoints implemented in [backend/src/main/java/com/primeestate/controller/AdminController.java](backend/src/main/java/com/primeestate/controller/AdminController.java). For the assignment these endpoints accept a `Role: ADMIN` request header — the frontend sends that header automatically when using the admin APIs.

- **Server-side roles (optional):** If you want persistent server-side admin roles later, add a role field to the `User` entity and return it on login, then enforce role checks server-side. This repo currently uses UI/client-side admin checks only.

If you want, I can (pick one):

- Add server-side `role` support and return it on login, or
- Harden the admin UI further (confirmation modals, pagination, search enhancements).
