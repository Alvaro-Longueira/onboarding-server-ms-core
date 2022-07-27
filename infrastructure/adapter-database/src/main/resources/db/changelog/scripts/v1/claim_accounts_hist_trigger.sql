CREATE TRIGGER claim_accounts_hist_trigger
AFTER INSERT OR UPDATE ON "claim_accounts"
FOR EACH ROW
  EXECUTE PROCEDURE claim_accounts_hist_fnc();