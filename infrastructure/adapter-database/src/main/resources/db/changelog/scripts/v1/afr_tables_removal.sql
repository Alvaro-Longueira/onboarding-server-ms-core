DROP TRIGGER IF EXISTS claim_accounts_hist_trigger ON claim_accounts;
DROP TRIGGER IF EXISTS claim_payments_hist_trigger ON claim_payments;

DROP FUNCTION IF EXISTS claim_accounts_hist_fnc;
DROP FUNCTION IF EXISTS claim_payments_hist_fnc;
