(function () {
  const token = localStorage.getItem('token');
  const role = localStorage.getItem('role');
  const username = localStorage.getItem('username');

  if (!token) { window.location.href = 'login.html'; return; }

  const page = location.pathname.split('/').pop() || '';

  const MENUS = {
    ADMIN: [
      { href: 'dashboard.html',      label: 'Dashboard' },
      { href: 'users.html',          label: 'Quản lý Khách hàng' },
      { href: 'sellers.html',        label: 'Quản lý Bác sĩ' },
      { href: 'categories.html',     label: 'Quản lý Chuyên khoa' },
      { href: 'appointments.html',   label: 'Lịch hẹn' },
      { href: 'audit-logs.html',     label: 'Lịch sử thay đổi' },
      { href: 'profile.html',        label: 'Hồ sơ' },
    ],
    PATIENT: [
      { href: 'specialties.html',     label: 'Đặt lịch khám' },
      { href: 'my-appointments.html', label: 'Lịch của tôi' },
      { href: 'profile.html',         label: 'Hồ sơ' },
    ],
    DOCTOR: [
      { href: 'doctor-appointments.html', label: 'Lịch hẹn' },
      { href: 'doctor-records.html',      label: 'Hồ sơ bệnh án' },
      { href: 'doctor-schedule.html',     label: 'Lịch làm việc' },
      { href: 'profile.html',             label: 'Hồ sơ' },
    ],
  };

  const nav = document.getElementById('sidebarNav');
  if (nav) {
    const items = MENUS[role] || [];
    nav.innerHTML = items.map(item =>
      `<a href="${item.href}"${page === item.href ? ' class="active"' : ''}>${item.label}</a>`
    ).join('');
  }

  const usernameEl = document.getElementById('sidebarUsername');
  if (usernameEl) usernameEl.textContent = username || '';

  const topbarUsernameEl = document.getElementById('topbarUsername');
  if (topbarUsernameEl) topbarUsernameEl.textContent = username || '';

  const roleBadge = document.getElementById('roleBadge');
  if (roleBadge) {
    const LABELS  = { ADMIN: 'ADMIN', PATIENT: 'PATIENT', DOCTOR: 'DOCTOR' };
    const CLASSES = { ADMIN: 'badge-admin', PATIENT: 'badge-user', DOCTOR: 'badge-doctor' };
    roleBadge.textContent = LABELS[role] || role || '';
    roleBadge.className   = `badge ${CLASSES[role] || ''}`;
  }
})();
