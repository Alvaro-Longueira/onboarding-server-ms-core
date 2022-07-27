CREATE TRIGGER claims_hist_trigger
AFTER INSERT OR UPDATE ON "claims"
FOR EACH ROW
  EXECUTE PROCEDURE claims_hist_fnc();