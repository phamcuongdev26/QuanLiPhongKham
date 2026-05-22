-- ============================================================
-- SHOPPE MVP - SEED DATA
-- Chạy file này sau khi tạo database
-- Lưu ý: App tự seed khi khởi động, file này chỉ để tham khảo
-- ============================================================

CREATE DATABASE IF NOT EXISTS shoppe_mvp_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE shoppe_mvp_db;

-- ── USERS ──────────────────────────────────────────────────
-- Password: Admin@123
INSERT IGNORE INTO users (username, full_name, email, password, role, is_active, created_at, updated_at) VALUES
('admin', 'Super Admin', 'admin@shoppe.com',
 '$2a$10$slYQmyNdgTY18LdlMqhIme5bIk6RlVAFxMpKNMC.5UqSqHKv6bC.a', 'ADMIN', 1, NOW(), NOW());

-- Password: User@123
INSERT IGNORE INTO users (username, full_name, email, password, role, is_active, created_at, updated_at) VALUES
('user1', 'Nguyễn Văn An',     'user1@shoppe.com',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LkdmMXXlBne', 'USER',   1, NOW(), NOW()),
('user2', 'Trần Thị Bình',     'user2@shoppe.com',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LkdmMXXlBne', 'USER',   1, NOW(), NOW()),
('user3', 'Lê Hoàng Cường',    'user3@shoppe.com',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LkdmMXXlBne', 'USER',   0, NOW(), NOW());

-- Password: Seller@123
INSERT IGNORE INTO users (username, full_name, email, password, role, is_active, created_at, updated_at) VALUES
('seller1', 'Shop Thời Trang ABC',     'seller1@shoppe.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LkdmMXXlBne', 'SELLER', 1, NOW(), NOW()),
('seller2', 'Cửa Hàng Điện Tử XYZ',   'seller2@shoppe.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LkdmMXXlBne', 'SELLER', 1, NOW(), NOW());

-- ── CATEGORIES ─────────────────────────────────────────────
INSERT IGNORE INTO categories (name, description, icon, active, created_at, updated_at) VALUES
('Quần áo',   'Thời trang nam, nữ, unisex',                    '👗', 1, NOW(), NOW()),
('Làm đẹp',   'Mỹ phẩm, chăm sóc da, trang điểm',             '💄', 1, NOW(), NOW()),
('Sách',      'Sách giáo khoa, tiểu thuyết, kỹ năng sống',     '📚', 1, NOW(), NOW()),
('Mẹ & Bé',   'Sản phẩm dành cho mẹ bầu và trẻ sơ sinh',      '🤱', 1, NOW(), NOW()),
('Trẻ em',    'Đồ chơi, quần áo, phụ kiện trẻ em',             '🧸', 1, NOW(), NOW()),
('Điện tử',   'Điện thoại, laptop, thiết bị điện tử',          '📱', 1, NOW(), NOW()),
('Nhà cửa',   'Nội thất, trang trí, dụng cụ gia đình',         '🏠', 1, NOW(), NOW()),
('Thể thao',  'Dụng cụ thể thao, quần áo thể thao',            '⚽', 1, NOW(), NOW()),
('Thực phẩm', 'Thực phẩm sạch, đồ uống, bánh kẹo',             '🍎', 1, NOW(), NOW()),
('Giày dép',  'Giày thể thao, sandal, dép, boots',             '👟', 1, NOW(), NOW()),
('Túi xách',  'Túi xách nữ, balo, ví da',                      '👜', 1, NOW(), NOW()),
('Đồng hồ',   'Đồng hồ nam, nữ, thông minh',                   '⌚', 1, NOW(), NOW());
