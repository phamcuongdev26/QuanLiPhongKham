(function () {
  const token = localStorage.getItem('token');
  const role = localStorage.getItem('role');
  const username = localStorage.getItem('username');

  if (!token) { window.location.href = 'login.html'; return; }

  const page = location.pathname.split('/').pop() || '';

  const MENUS = {
    ADMIN: [
      { href: 'dashboard.html',   icon: '&#9632;',   label: 'Dashboard' },
      { href: 'users.html',       icon: '&#128100;', label: 'Quản lý Khách hàng' },
      { href: 'sellers.html',     icon: '&#129657;', label: 'Bác sĩ' },
      { href: 'categories.html',  icon: '&#127991;', label: 'Chuyên khoa' },
      { href: 'audit-logs.html',  icon: '&#128203;', label: 'Lịch sử thay đổi' },
      { href: 'profile.html',     icon: '&#128196;', label: 'Hồ sơ' },
    ],
    PATIENT: [
      { href: 'specialties.html',     icon: '&#127973;', label: 'Đặt lịch khám' },
      { href: 'my-appointments.html', icon: '&#128197;', label: 'Lịch của tôi' },
      { href: 'profile.html',         icon: '&#128100;', label: 'Hồ sơ' },
    ],
    DOCTOR: [
      { href: 'doctor-appointments.html', icon: '&#128197;', label: 'Lịch hẹn' },
      { href: 'doctor-records.html',      icon: '&#128203;', label: 'Hồ sơ bệnh án' },
      { href: 'doctor-schedule.html',     icon: '&#128336;', label: 'Lịch làm việc' },
      { href: 'profile.html',             icon: '&#128100;', label: 'Hồ sơ' },
    ],
  };

  const nav = document.getElementById('sidebarNav');
  if (nav) {
    const items = MENUS[role] || [];
    nav.innerHTML = items.map(item =>
      `<a href="${item.href}"${page === item.href ? ' class="active"' : ''}><span class="icon">${item.icon}</span> ${item.label}</a>`
    ).join('');
  }

  const usernameEl = document.getElementById('sidebarUsername');
  if (usernameEl) usernameEl.textContent = username || '';

  const topbarUsernameEl = document.getElementById('topbarUsername');
  if (topbarUsernameEl) topbarUsernameEl.textContent = username || '';

  const roleBadge = document.getElementById('roleBadge');
  if (roleBadge) {
    const LABELS = { ADMIN: 'ADMIN', PATIENT: 'PATIENT', DOCTOR: 'DOCTOR' };
    const CLASSES = { ADMIN: 'badge-admin', PATIENT: 'badge-user', DOCTOR: 'badge-doctor' };
    roleBadge.textContent = LABELS[role] || role || '';
    roleBadge.className = `badge ${CLASSES[role] || ''}`;
  }
})();
