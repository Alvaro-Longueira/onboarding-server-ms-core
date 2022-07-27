CREATE OR REPLACE FUNCTION claims_hist_fnc() 
RETURNS trigger 
LANGUAGE 'plpgsql'
AS $$
BEGIN

  INSERT INTO "hist_claims" 
  ("id", "claim_id", "event_date","notification_date","entry_date","contract_id",
  "offer_id","account_id", "type", "coverage_id", "status", "product_id", 
  "product_description", "insurance_id", "without_coverage", "place_of_event", "description",
  "symass_id", "version", "created_at", "created_by", "updated_at", "updated_by")
  VALUES
  (NEW."id",NEW."claim_id",NEW."event_date",NEW."notification_date",NEW."entry_date",NEW."contract_id",
  NEW."offer_id",NEW."account_id", NEW."type", NEW."coverage_id", NEW."status", NEW."product_id", 
  NEW."product_description", NEW."insurance_id", NEW."without_coverage", NEW."place_of_event", NEW."description",
  NEW."symass_id", NEW."version", NEW."created_at", NEW."created_by", NEW."updated_at", NEW."updated_by");

  RETURN NEW;
END;
$$
