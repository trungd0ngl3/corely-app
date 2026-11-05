# Corely API reference

Frontend-facing notes for the Spring Boot API. Verified against the running
backend's OpenAPI document on 2026-11-05.

## Base URL and source of truth

- Local backend: `http://localhost:8080`
- Most endpoints are under `/api/v1`.
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- Use the live OpenAPI document for exact response DTOs and changes not listed
  here.

## Authentication and response format

- Login is `POST /api/v1/auth/login` with `{ "email": "...", "password": "..." }`.
  The response result contains `token`, `refreshToken`, and `isAuth`.
- Send the access token on protected API requests as
  `Authorization: Bearer <token>`. Refresh, logout, and introspection requests
  send the token in a JSON body.
- Auth routes, product routes, category routes, and brand routes are public.
  Other routes require authentication unless the backend grants access through
  a specific rule. Some write operations additionally require a role; for
  example, product creation, update, and deletion require `ADMIN` or `SELLER`.
- Most JSON endpoints use the envelope
  `{ "code": ..., "message": ..., "result": ... }`. Catalog reads currently
  return `code: 0`; other controller helpers may return `code: 1000`. Use the
  HTTP status and `result` shape to determine whether a request succeeded; do
  not assume one success code. The `result` can be absent/null for void
  operations. Do not expect this envelope from the payment endpoints: they
  return a raw string or payment DTO.
- Send JSON request bodies with `Content-Type: application/json`. Image upload
  is `multipart/form-data` with a `file` part and optional `folder` query
  parameter.

## Endpoint index

### Authentication and account

| Method | Path | Purpose |
|---|---|---|
| POST | `/api/v1/auth/register` | Register a user |
| POST | `/api/v1/auth/login` | Authenticate and obtain tokens |
| POST | `/api/v1/auth/tokens/refresh` | Refresh tokens |
| POST | `/api/v1/auth/tokens/logout` | Log out a token |
| POST | `/api/v1/auth/tokens/introspect` | Check a token |
| GET | `/api/v1/auth/verify-email?token=...` | Verify an email address |
| POST | `/api/v1/auth/resend-verification` | Resend verification email |
| POST | `/api/v1/auth/forgot-password` | Start password recovery |
| POST | `/api/v1/auth/reset-password` | Reset password with token |
| PATCH | `/api/v1/auth/change-password` | Change password |
| GET, PUT, DELETE | `/api/v1/users/me` | Read, update, or delete current user |

### Products, categories, brands, and store

| Method | Path | Purpose |
|---|---|---|
| GET | `/api/v1/products` | Search/filter products; accepts `q`, `categoryId`, `brandId`, `storeId`, `minPrice`, `maxPrice`, and pageable query parameters |
| GET | `/api/v1/products/{slug}` | Get product by slug |
| POST | `/api/v1/products` | Create product (`ADMIN` or `SELLER`) |
| PUT, DELETE | `/api/v1/products/{id}` | Update or delete product by UUID (`ADMIN` or `SELLER`) |
| GET, POST | `/api/v1/products/{productId}/reviews` | List paginated reviews or create a review |
| PUT, DELETE | `/api/v1/reviews/{id}` | Update or delete a review by UUID |
| GET, POST | `/api/v1/categories` | List or create categories |
| GET | `/api/v1/categories/{slug}` | Get category by slug |
| PUT, DELETE | `/api/v1/categories/{id}` | Update or delete category by UUID |
| GET, POST | `/api/v1/brands` | List or create brands |
| GET | `/api/v1/brands/{slug}` | Get brand by slug |
| PUT, DELETE | `/api/v1/brands/{id}` | Update or delete brand by UUID |
| GET, PUT | `/api/v1/store` | Read or update the store |

### Cart and wishlist

| Method | Path | Purpose |
|---|---|---|
| GET, DELETE | `/api/v1/cart` | Read or clear cart |
| POST, PUT | `/api/v1/cart/items` | Add or update an item |
| DELETE | `/api/v1/cart/items/{productId}?variantId=...` | Remove item (variant ID is optional) |
| GET | `/api/v1/cart/stock-check` | Check cart item stock |
| POST, DELETE | `/api/v1/cart/voucher` | Apply voucher (`voucherCode`) or remove it |
| GET | `/api/v1/wishlist` | Read wishlist |
| POST, DELETE | `/api/v1/wishlist/{productId}` | Add or remove a product |

### Addresses, checkout, orders, and payments

| Method | Path | Purpose |
|---|---|---|
| GET, POST | `/api/v1/addresses` | List or create addresses |
| PUT, DELETE | `/api/v1/addresses/{id}` | Update or delete address by UUID |
| PATCH | `/api/v1/addresses/{id}/default` | Set default address |
| GET | `/api/v1/checkout-config/shipping-methods` | List shipping methods |
| GET | `/api/v1/checkout-config/payment-methods` | List payment methods |
| POST | `/api/v1/orders` | Create order |
| GET | `/api/v1/orders/my-order` | List current user's paginated orders |
| GET | `/api/v1/orders/store/{storeId}` | List store's paginated orders |
| PUT | `/api/v1/orders/{orderId}/status` | Update order status |
| POST | `/api/v1/payments/{orderId}/vnpay-url` | Create VNPay URL (raw string response) |
| GET | `/api/v1/payments/vnpay/callback` | VNPay callback; accepts `vnp_*` query fields (raw payment DTO response) |
| GET | `/api/v1/payments/order/{orderId}` | Get payment for order (raw payment DTO response) |

### Administration, uploads, and utility

| Method | Path | Purpose |
|---|---|---|
| GET | `/api/v1/admin/dashboard/summary` | Dashboard summary |
| GET | `/api/v1/admin/dashboard/revenues?days=...` | Revenue chart |
| GET | `/api/v1/admin/dashboard/products/top?limit=...` | Top products |
| GET | `/api/v1/admin/dashboard/Order?limit=...` | Dashboard orders (path capitalization is intentional) |
| GET, POST | `/api/v1/roles` | List or create roles |
| DELETE | `/api/v1/roles/{name}` | Delete role |
| POST | `/upload/image?folder=...` | Upload image; `folder` defaults to `GENERAL` |
| DELETE | `/upload/image/{publicId}` | Delete uploaded image |
| GET | `/api/test/mail` | Backend mail test endpoint |

## Common request shapes

Fields below are from the live OpenAPI DTOs. Check that document for required
fields, validation constraints, enums, nested types, and response schemas.

```json
// POST /api/v1/auth/register
{
  "email": "user@example.com",
  "password": "...",
  "fullName": "...",
  "dateOfBirth": "YYYY-MM-DD",
  "phone": "..."
}
```

```json
// POST /api/v1/cart/items and PUT /api/v1/cart/items
{ "productId": "UUID", "variantId": "UUID", "quantity": 1 }
```

```json
// POST /api/v1/orders
{
  "storeId": "UUID",
  "shippingAddress": "...",
  "shippingMethod": "...",
  "paymentMethod": "..."
}
```

Product create/update uses `ProductCreationRequest`: `name`, `sku`, `price`,
`discountPrice`, `stockQuantity`, `description`, `specs`, `thumbnailUrl`,
`storeId`, `categoryId`, `brandId`, `imageUrls`, and `variants`.

Other request DTOs:

- Profile update: `fullName`, `phone`, `avatarUrl`, `dateOfBirth`.
- Address: `streetAddress`, `ward`, `district`, `city`, `isDefault`.
- Review: `rating`, `comment`.
- Category: `name`, `description`; brand: `name`.
- Store update: `name`, `description`, `logoUrl`, `bannerUrl`, `contactPhone`,
  `contactEmail`, `address`.
- Order status: `status`; voucher: `voucherCode`.

## Pagination and implementation notes

- Endpoints that accept Spring `Pageable` use `page`, `size`, and `sort`
  query parameters. Product search and review listing also accept pageable
  parameters; order listing endpoints are paginated.
- Product detail and product reviews use UUID parameters, while product
  detail-by-slug uses a slug. Do not interchange these identifiers.
- Check `/v3/api-docs` before implementing a new call: it is the authoritative
  current contract, particularly for response fields and endpoint-specific
  access rules.

## Landing page integration

The home page uses `services/catalog-service.ts` for public catalog requests:

- `GET /api/v1/products?page=0&size=24` supplies the product cards, hero
  product, budget suggestion, and total product count.
- `GET /api/v1/categories` supplies the category tiles.
- The frontend reads the `result` from each API envelope and validates the
  returned product-page/category shapes. It displays a loading state, and
  surfaces request errors with a retry action.
- API base URL defaults to `http://localhost:8080`. Set
  `NEXT_PUBLIC_API_BASE_URL` when the backend runs at a different origin; the
  backend CORS configuration must allow the frontend origin.
- Product prices use `discountPrice` when it is a positive value below `price`;
  otherwise they use `price`. The original price is shown as the crossed-out
  price when discounted. Product fields and category names come from the API.
- No local demo product list, demo imagery, fabricated review ratings, or
  pre-filled sample cart is used by the landing page. When the API has no
  product image, the page displays a neutral placeholder instead.
- Previously saved rows from the prototype's seeded cart are removed when the
  cart is loaded; new carts start empty.
- The budget selector recommends an API product by its current price; it does
  not assemble or configure a multi-part PC build. Add-to-cart still uses the
  browser's local cart and is not wired to the backend cart endpoints.
- The separate product listing and detail experiences still contain prototype
  data and are not part of this landing-page API integration.
