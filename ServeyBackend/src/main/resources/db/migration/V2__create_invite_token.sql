-- Bewusst ohne Bezug zu "response" und ohne Zeitstempel: so laesst sich keine Antwort einer Person zuordnen.
CREATE TABLE invite_token (
  token text    PRIMARY KEY,
  used  boolean NOT NULL DEFAULT false
);
-- Tokens erzeugen (z.B. 40 Stueck), danach exportieren und als Links verteilen:
--   INSERT INTO invite_token (token)
--   SELECT left(replace(gen_random_uuid()::text,'-',''),12) FROM generate_series(1,40);
--   SELECT 'https://deine-domain/?t=' || token FROM invite_token WHERE NOT used;
