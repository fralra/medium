
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
  v_counter_AM number := 0;
  v_counter_other number := 0;
  v_lap_sum_EU number := 0;
  v_lap_sum_AM number := 0;
  v_lap_sum_other number := 0;

  CURSOR c_lap_info IS
     select r.NAME as circuit_name, l.raceid, l.driverid, d.DRIVERREF, d.nationality, l.LAP, l.MILLISECONDS
      from laptimes l
      join races r on l.raceid = r.raceid
      join drivers d on l.driverid = d.driverid;
 
   r_lap_info c_lap_info%ROWTYPE;
BEGIN

  OPEN c_lap_info;

  LOOP
    FETCH  c_lap_info  INTO r_lap_info;
    EXIT WHEN c_lap_info%NOTFOUND;

  CASE 
    WHEN r_lap_info.nationality in ('British','German','Spanish','Finnish','French',
    'Polish','Italian','Austrian','Dutch','Portuguese','Irish','Danish',
    'Hungarian','Czech','Swiss','Belgian','Monegasque','Swedish','East German',
    'Liechtensteiner'
      ) THEN
      v_counter_EU := v_counter_EU + 1;
      v_lap_sum_EU := v_lap_sum_EU + r_lap_info.MILLISECONDS/1000; 
    WHEN r_lap_info.nationality in ('Canadian','American','Brazilian','Colombian',
       'Argentine','Venezuelan','Chilean','Mexican','Uruguayan'
      ) THEN
      v_counter_AM := v_counter_AM + 1;
      v_lap_sum_AM := v_lap_sum_AM + r_lap_info.MILLISECONDS/1000;      
    ELSE
      v_counter_other := v_counter_other + 1;
      v_lap_sum_other := v_lap_sum_other + r_lap_info.MILLISECONDS/1000;
    END CASE;
    
    v_counter := v_counter +1;

 END LOOP;
  CLOSE c_lap_info;
  DBMS_OUTPUT.PUT_LINE( 'EU lap (seconds) ' || v_lap_sum_EU || ', races: ' || v_counter_EU );
  DBMS_OUTPUT.PUT_LINE( 'AM lap (seconds) ' || v_lap_sum_AM || ', races: ' || v_counter_AM );
  DBMS_OUTPUT.PUT_LINE( 'Other lap (seconds) ' || v_lap_sum_other || ', races: ' || v_counter_other );
  DBMS_OUTPUT.PUT_LINE( 'Total races: ' || v_counter || '.' );

END FN_LAP_STATS;
END;
/
