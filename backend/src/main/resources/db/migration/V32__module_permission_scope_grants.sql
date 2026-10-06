-- Runtime scope replacement needs DELETE on access_scope only; business ledgers remain append-only.
DO $$ BEGIN
  IF EXISTS (SELECT 1 FROM pg_roles WHERE rolname='core_app') THEN
    GRANT DELETE ON retail.access_scope TO core_app;
  END IF;
END $$;
