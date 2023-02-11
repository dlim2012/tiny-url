CREATE DATABASE IF NOT EXISTS tiny_url;

--USE tiny_url;


--CREATE EVENT clean_confirmation_token ON schedule every 1 DAY ENABLE
--    DO DELETE FROM confirmation_token
--    WHERE `created_at` < CURRENT_TIMESTAMP - INTERVAL 15 MINUTE;
--
--
--CREATE EVENT clean_token ON schedule every 1 DAY ENABLE
--    DO DELETE FROM token
--    WHERE expire_date < CURDATE() + INTERVAL 2 YEAR;
--
--
--CREATE EVENT clean_token ON schedule every 1 DAY ENABLE
--    DO DELETE FROM token
--    WHERE expire_date < CURDATE() + INTERVAL 2 YEAR;
