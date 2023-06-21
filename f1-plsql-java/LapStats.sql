
create or replace package lap_stats_plsql
IS
 PROCEDURE FN_LAP_STATS;
END;
/

  
create or replace package body lap_stats_plsql
IS
PROCEDURE FN_LAP_STATS
IS
  v_counter number := 0;
  v_counter_EU number := 0;
  v_counter_noEU number := 0;
  v_lap_sum_EU number := 0;
  v_lap_sum_noEU number := 0;

   -- cursor
  CURSOR c_lap_info IS
     select r.NAME as circuit_name, l.raceid, l.driverid, d.DRIVERREF, d.nationality, l.LAP, l.MILLISECONDS
      from laptimes l
      join races r on l.raceid = r.raceid
      join drivers d on l.driverid = d.driverid;
   -- record    
   r_lap_info c_lap_info%ROWTYPE;
BEGIN

  OPEN c_lap_info;

  LOOP
    FETCH  c_lap_info  INTO r_lap_info;
    EXIT WHEN c_lap_info%NOTFOUND;

  CASE 
    WHEN r_lap_info.nationality in ('British', 'German', 'Finnish', 'French', 'Spanish', 'Italian', 'Dutch','Polish') THEN
      v_counter_EU := v_counter_EU + 1;
      v_lap_sum_EU := v_lap_sum_EU + r_lap_info.MILLISECONDS/1000;
    ELSE
      v_counter_noEU := v_counter_noEU + 1;
      v_lap_sum_noEU := v_lap_sum_noEU + r_lap_info.MILLISECONDS/1000;
    END CASE;
    
    v_counter := v_counter +1;

 END LOOP;
  CLOSE c_lap_info;
  DBMS_OUTPUT.PUT_LINE( 'EU lap (seconds) ' || v_lap_sum_EU || ', races: ' || v_counter_EU );
  DBMS_OUTPUT.PUT_LINE( 'NoEU lap (seconds) ' || v_lap_sum_noEU || ', races: ' || v_counter_noEU );
  DBMS_OUTPUT.PUT_LINE( 'Total races: ' || v_counter || '.' );

END FN_LAP_STATS;
END;
/
