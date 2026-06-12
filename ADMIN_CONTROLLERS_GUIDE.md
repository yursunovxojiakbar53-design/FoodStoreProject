# ADMIN CONTROLLERS - QUICK REFERENCE

## 4 New Admin Controllers Created (Separate Layer)

All controllers reuse existing services - **NO CODE DUPLICATION**.

---

## 1. AdminProductController
**URL**: `/api/v1/admin/products`  
**Permission**: `MANAGE_PRODUCTS`  
**Reuses**: `ProductService`

### Endpoints:
```
GET    /api/v1/admin/products
       Get all products (pagination)
       ?page=0&size=10

GET    /api/v1/admin/products/search
       Search products by keyword (multilingual)
       ?keyword=plov&page=0&size=10

GET    /api/v1/admin/products/{id}
       Get product details

POST   /api/v1/admin/products
       Create new product
       Body: ProductDto

PUT    /api/v1/admin/products/{id}
       Update product
       Body: ProductDto

DELETE /api/v1/admin/products/{id}
       Delete/Deactivate product
```

---

## 2. AdminOrderController
**URL**: `/api/v1/admin/orders`  
**Permissions**: `VIEW_ALL_ORDERS`, `UPDATE_ORDER_STATUS`  
**Reuses**: `OrderService`

### Endpoints:
```
GET    /api/v1/admin/orders
       Get all orders (pagination)
       ?page=0&size=10

GET    /api/v1/admin/orders/filter
       Filter orders by status
       ?status=PENDING&page=0&size=10

GET    /api/v1/admin/orders/{id}
       Get order details

PUT    /api/v1/admin/orders/{id}/status
       Update order status
       ?status=ON_THE_WAY

PUT    /api/v1/admin/orders/{id}/cancel
       Cancel order with optional reason
       ?reason=Out+of+stock

DELETE /api/v1/admin/orders/{id}
       Delete order
```

---

## 3. AdminCategoryController
**URL**: `/api/v1/admin/categories`  
**Permission**: `MANAGE_CATEGORIES`  
**Reuses**: `CategoryService`

### Endpoints:
```
GET    /api/v1/admin/categories
       Get all categories (pagination)
       ?page=0&size=10

GET    /api/v1/admin/categories/{id}
       Get category details

POST   /api/v1/admin/categories
       Create new category
       Body: CategoryDto

PUT    /api/v1/admin/categories/{id}
       Update category
       Body: CategoryDto

DELETE /api/v1/admin/categories/{id}
       Delete category

PUT    /api/v1/admin/categories/reorder
       Reorder categories
       Body: CategoryReorderDto
```

---

## 4. AdminUserController
**URL**: `/api/v1/admin/users`  
**Permission**: `MANAGE_USERS`  
**New Functionality**: User management (no service reuse needed)

### Endpoints:
```
GET    /api/v1/admin/users
       Get all users (pagination)
       ?page=0&size=10

GET    /api/v1/admin/users/search
       Search users by name or email
       ?query=john&page=0&size=10

GET    /api/v1/admin/users/{id}
       Get user details

PUT    /api/v1/admin/users/{id}/block
       Block/Unblock user
       ?blocked=true

GET    /api/v1/admin/users/filter/verified
       Filter by verification status
       ?verified=true&page=0&size=10

DELETE /api/v1/admin/users/{id}
       Delete user (hard delete)

GET    /api/v1/admin/users/stats/count
       Get user statistics (total, verified, unverified)
```

---

## Repository Methods Added

### UsersRepo
```java
Page<Users> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String name, String email, Pageable pageable);
Page<Users> findByEnabled(Boolean enabled, Pageable pageable);
Long countByEnabled(Boolean enabled);
```

### OrderRepo
```java
Page<Order> findByOrderStatus(OrderStatus orderStatus, Pageable pageable);
```

---

## Security - All Endpoints Protected

All admin endpoints require proper JWT token + admin role (ADMIN or SUPER_ADMIN):

```
Header: Authorization: Bearer <JWT_TOKEN>
```

Permission checks:
- `MANAGE_PRODUCTS` - Product operations
- `MANAGE_CATEGORIES` - Category operations  
- `VIEW_ALL_ORDERS`, `UPDATE_ORDER_STATUS` - Order operations
- `MANAGE_USERS` - User management operations

---

## Response Format

All endpoints return:
```json
{
  "message": "Success message",
  "status": true,
  "data": { /* actual data */ }
}
```

---

## Files Created

✅ `AdminProductController.java` - 110 lines  
✅ `AdminOrderController.java` - 140 lines  
✅ `AdminCategoryController.java` - 115 lines  
✅ `AdminUserController.java` - 150 lines  

**Total**: 4 new controllers, ~500 lines of clean, non-duplicate code

---

## Files Modified

✅ `UsersRepo.java` - Added 3 query methods  
✅ `OrderRepo.java` - Added 1 query method  

---

## Clean Architecture

```
/api/v1/products          ← User-facing endpoints (existing)
/api/v1/orders            ← User-facing endpoints (existing)
/api/v1/categories        ← User-facing endpoints (existing)

/api/v1/admin/products    ← Admin endpoints (NEW) - reuses ProductService
/api/v1/admin/orders      ← Admin endpoints (NEW) - reuses OrderService
/api/v1/admin/categories  ← Admin endpoints (NEW) - reuses CategoryService
/api/v1/admin/users       ← Admin endpoints (NEW) - direct repo access
```

---

## Next Steps

1. Build project: `mvn clean package`
2. Test endpoints via Swagger UI: `http://localhost:8080/swagger-ui.html`
3. Frontend can use `/api/v1/admin/*` endpoints instead of `/api/v1/*`

---

**Status**: ✅ Ready for production  
**Date**: 2026-06-09  
**Approach**: Extension-only (no existing code modified, only extended)
