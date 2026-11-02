<!-- BEGIN:nextjs-agent-rules -->

## This is NOT the Next.js you know

This version has breaking changes — APIs, conventions, and file structure may all differ from your training data. Read the relevant guide in `node_modules/next/dist/docs/` (resolved from this file's directory; in monorepos the `next` package may not be visible from the repo root) before writing any code. Heed deprecation notices.

This block is written and re-added by `next dev` — verify at `node_modules/next/dist/server/lib/generate-agent-files.js`. Removing it from a diff only re-creates the uncommitted change; committing it with your work keeps the tree clean.

<!-- END:nextjs-agent-rules -->


## Brand & Style

This design system is engineered for the high-performance computing market. It balances technical authority with a streamlined, premium retail experience. The brand personality is **precise, powerful, and dependable**, mirroring the hardware it showcases.

The visual style is **Corporate Modern with a Minimalist edge**. It avoids the cluttered "gamer" aesthetic often found in the industry, opting instead for a clean, editorial-tech approach. This ensures that technical specifications remain legible while high-resolution product imagery creates a sense of luxury and performance. The UI utilizes generous whitespace to allow complex hardware components to breathe, framing them as sophisticated pieces of engineering rather than commodities.

## Colors

The palette is anchored by **Electric Blue**, a color synonymous with trust, electricity, and high-speed data. **Dark Slate** provides deep contrast for headers and navigation, grounding the interface in professional weight. 

**Vivid Orange** is used exclusively as a high-intent accent for time-sensitive "Flash Sales" or call-to-action highlights, ensuring they cut through the cool-toned primary palette. The background uses a soft **Slate White** to reduce eye strain during prolonged browsing of technical spec sheets, while semantic colors (Success/Danger) maintain standard accessibility for transaction and status flows.

## Typography

This design system utilizes **Inter** for all roles to leverage its exceptional legibility and systematic appearance. The type hierarchy is designed to handle dense technical data without overwhelming the user.

- **Display & Headlines:** Use tight letter-spacing and bold weights to project strength and architectural structure.
- **Body Text:** Optimized for reading long lists of product features and compatibility notes.
- **Spec-code:** A specialized variant for technical data points (e.g., clock speeds, voltages) utilizing tabular figures to ensure numbers align perfectly in comparison tables.
- **Labels:** Uppercase styling is used for secondary metadata and category tags to differentiate them from interactive body text.

## Layout & Spacing

The layout follows a **12-column fluid grid** for desktop, transitioning to a **4-column grid** for mobile devices. It utilizes a 4px baseline grid to ensure mathematical harmony across all components.

- **Desktop (1280px+):** Centered fixed-width container with 24px gutters. Use large `xl` padding for landing sections to emphasize the premium nature of the products.
- **Tablet (768px - 1279px):** Content expands to fill the screen width with 32px side margins. Grid transitions to 8 columns.
- **Mobile (Below 768px):** 16px side margins. Stacks complex data tables into vertical cards or horizontally scrollable containers.

Whitespace is used strategically to separate product categories. Higher-priced flagship items should be given significantly more "breathing room" (margins) than budget components to visually signal their premium status.

## Elevation & Depth

To maintain a modern, high-tech feel, this design system uses **Tonal Layers** combined with **Ambient Shadows**. Depth is used to guide the user's focus through the purchasing funnel.

- **Level 0 (Background):** #F8FAFC. The canvas for all content.
- **Level 1 (Cards/Surfaces):** White (#FFFFFF) with a very soft, 4% opacity indigo-tinted shadow. Used for standard product cards and content blocks.
- **Level 2 (Interactive/Hover):** A slightly more pronounced shadow (8% opacity) with a 1px soft border in #E2E8F0. Used for active cards or items the user is interacting with.
- **Level 3 (Overlays/Modals):** High-diffusion shadows to create a physical sense of "lift" over the interface.

Avoid harsh black shadows; instead, use the Secondary color (#0F172A) at very low opacities to keep the shadows feeling integrated and clean.

## Shapes

The shape language reflects the precision of hardware manufacturing—structured but refined. 

- **Standard Elements:** Buttons, input fields, and small UI tags use a **0.5rem (8px)** radius.
- **Containers:** Product cards and content sections use a **1rem (16px)** radius to create a softer, more modern enclosure.
- **Promotional Banners:** Can use the **1.5rem (24px)** radius to feel more approachable and distinct from the more "rigid" technical parts of the site.

The consistency of these radii creates a "friendly-professional" balance that differentiates the store from older, more industrial-looking hardware competitors.

## Components

### Buttons
- **Primary:** Solid #2563EB with white text. High-contrast, 0.5rem radius. 
- **Secondary:** Transparent background with a 1px #2563EB border. Used for "View Details" or "Add to Compare."
- **Flash Sale:** Solid #F97316 for high-urgency checkout or limited offers.

### Product Cards
Cards are the core of this design system. They must include:
- A clean, 1:1 aspect ratio image container with a light gray background (#F1F5F9).
- A clear hierarchy: Brand (Small Label) > Product Name (Headline-sm) > Price (Primary Color, Bold).
- Floating "Compare" toggle in the top-right corner.

### Input Fields
- Structured with 1px #CBD5E1 borders.
- On focus, the border transitions to Primary Blue with a subtle outer glow (2px spread, 10% opacity).

### Technical Spec Tables
- Alternate row striping using #F8FAFC and #FFFFFF.
- Labels in Secondary Slate for high readability.
- Values in #334155 (Dark Gray) for a softer, professional look.

### Search Bar
- A dominant, wide component in the header. 
- Features a "Quick Results" dropdown that categorizes results by "Components," "Pre-built PCs," and "Brands."