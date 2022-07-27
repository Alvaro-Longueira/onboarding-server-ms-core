CREATE TRIGGER claim_payments_hist_trigger
AFTER INSERT OR UPDATE ON "claim_payments"
FOR EACH ROW
  EXECUTE PROCEDURE claim_payments_hist_fnc();