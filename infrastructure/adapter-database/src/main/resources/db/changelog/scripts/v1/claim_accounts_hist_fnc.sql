CREATE OR REPLACE FUNCTION claim_accounts_hist_fnc() 
RETURNS trigger 
LANGUAGE 'plpgsql'
AS $$
BEGIN

  INSERT INTO "hist_claim_accounts" 
  ("id", "balance", "currency","type","claim_id",
   "version", "created_at", "created_by", "updated_at", "updated_by")
  VALUES
  (NEW."id",NEW."balance",NEW."currency",NEW."type",NEW."claim_id",
   NEW."version", NEW."created_at", NEW."created_by", NEW."updated_at", NEW."updated_by");

  RETURN NEW;
END;
$$
