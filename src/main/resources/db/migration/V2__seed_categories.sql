-- Flyway V2: seed core categories and subcategories
INSERT INTO categories(name) VALUES
 ('Electrical'),
 ('Plumbing'),
 ('Cementing & Flooring'),
 ('Wall Construction'),
 ('Cleaning'),
 ('AC Repair'),
 ('Tutoring')
ON CONFLICT DO NOTHING;

INSERT INTO sub_categories(category_id, name)
SELECT c.id, s.name FROM (
  VALUES
   ('Electrical','Wiring & Switches'),
   ('Electrical','Fan Installation'),
   ('Plumbing','Leak Fix'),
   ('Plumbing','New Fittings'),
   ('Cementing & Flooring','Tile Laying'),
   ('Cementing & Flooring','Marble/Granite'),
   ('Wall Construction','Brick Work'),
   ('Wall Construction','Plastering'),
   ('Cleaning','Home Deep Clean'),
   ('AC Repair','Gas Refill'),
   ('Tutoring','Maths'),
   ('Tutoring','English')
) AS s(category, name)
JOIN categories c ON c.name = s.category
ON CONFLICT DO NOTHING;
