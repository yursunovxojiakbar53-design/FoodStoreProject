-- foodstore_schema.sql
-- SQLite schema for FoodStore

PRAGMA foreign_keys = ON;

-- Users / customers
CREATE TABLE IF NOT EXISTS users (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  name TEXT NOT NULL,
  email TEXT NOT NULL UNIQUE,
  password_hash TEXT,
  phone TEXT,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Product categories
CREATE TABLE IF NOT EXISTS categories (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  name TEXT NOT NULL UNIQUE,
  description TEXT
);

-- Products
CREATE TABLE IF NOT EXISTS products (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  name TEXT NOT NULL,
  description TEXT,
  price REAL NOT NULL DEFAULT 0.0,
  sku TEXT UNIQUE,
  category_id INTEGER REFERENCES categories(id) ON DELETE SET NULL,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_products_sku ON products(sku);
CREATE INDEX IF NOT EXISTS idx_products_category ON products(category_id);

-- Inventory (simple stock table)
CREATE TABLE IF NOT EXISTS inventory (
  product_id INTEGER PRIMARY KEY REFERENCES products(id) ON DELETE CASCADE,
  quantity INTEGER NOT NULL DEFAULT 0,
  location TEXT
);

-- Orders
CREATE TABLE IF NOT EXISTS orders (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  user_id INTEGER REFERENCES users(id) ON DELETE SET NULL,
  status TEXT NOT NULL DEFAULT 'pending',
  total_amount REAL NOT NULL DEFAULT 0.0,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_orders_user ON orders(user_id);

-- Order items (line items)
CREATE TABLE IF NOT EXISTS order_items (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  order_id INTEGER NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
  product_id INTEGER NOT NULL REFERENCES products(id) ON DELETE RESTRICT,
  quantity INTEGER NOT NULL DEFAULT 1,
  unit_price REAL NOT NULL DEFAULT 0.0
);
CREATE INDEX IF NOT EXISTS idx_order_items_order ON order_items(order_id);

-- Example seed data (optional)
-- INSERT INTO categories(name, description) VALUES ('Beverages', 'Drinks and refreshments');
-- INSERT INTO products(name, description, price, sku, category_id) VALUES ('Apple Juice', '500ml bottle', 2.5, 'AJ500', 1);

-- To create a SQLite DB from this schema:
--   sqlite3 foodstore.db < foodstore_schema.sql

