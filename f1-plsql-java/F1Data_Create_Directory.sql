CREATE OR REPLACE DIRECTORY f1_csv_dir AS '/home/oracle/f1data'
/
GRANT READ, WRITE ON DIRECTORY f1_csv_dir TO f1data
/
EXIT
