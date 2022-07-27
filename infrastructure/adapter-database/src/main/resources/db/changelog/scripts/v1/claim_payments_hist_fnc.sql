CREATE OR REPLACE FUNCTION claim_payments_hist_fnc() 
RETURNS trigger 
LANGUAGE 'plpgsql'
AS $$
BEGIN

  INSERT INTO "hist_claim_payments" 
  ("id", "claim_payment_id", "amount","currency","payment_reference","receiver_id",
  "receiver_type","entry_date", "salesforce_id", "symass_id", "claim_id", "e2e_external_id", 
  "status", "sent_to_payout_date", "payment_date", "returned_payment_date", "version", 
  "created_at", "created_by", "updated_at", "updated_by")
  VALUES
  (NEW."id",NEW."claim_payment_id",NEW."amount",NEW."currency",NEW."payment_reference",NEW."receiver_id",
  NEW."receiver_type",NEW."entry_date", NEW."salesforce_id", NEW."symass_id", NEW."claim_id", NEW."e2e_external_id", 
  NEW."status", NEW."sent_to_payout_date", NEW."payment_date", NEW."returned_payment_date", NEW."version",
  NEW."created_at", NEW."created_by", NEW."updated_at", NEW."updated_by");

  RETURN NEW;
END;
$$
