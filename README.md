# Humidity_Sensor_Data_Cli
*****  Sensor Statistics Application *****
This is based on scala, cats effect, fs2 libraries command line application that calculates statistics from humidity sensor data.

The sensors are in a network, and they are divided into groups. Each sensor submits its data to its group leader.Each leader produces a daily report file for a group. 
The network periodically re-balances itself, so the sensors could change the group assignment over time, and their measurements can be reported by different leaders. 
The program should help spot sensors with highest average humidity.

## Input to the program should be as follows:
- Program takes one argument: a path to directory
- Directory contains many CSV files (*.csv), each with a daily report from one group leader
- Format of the file: 1 header line + many lines with measurements
- Measurement line has sensor id and the humidity value
- Humidity value is integer in range `[0, 100]` or `NaN` (failed measurement)
- The measurements for the same sensor id can be in the different files

## Expected Output should be as follows:
Program prints statistics to StdOut:
- It reports - how many files it processed
- It reports - how many measurements it processed
- It reports - how many measurements failed
- For each sensor it calculates min/avg/max humidity
- `NaN` values are ignored from min/avg/max
- Sensors with only `NaN` measurements have min/avg/max as `NaN/NaN/NaN`
- Program sorts sensors by highest avg humidity (`NaN` values go last)

## Important Notes
- Use scala 2.13.x
- Single daily report file can be very large, and can exceed program memory
- Program should only use memory for its internal state (no disk, no database)
- Implement a purely functional solution using cats-effect, fs2.

### Example input directory havinf two csv files

leader-1.csv
```
sensor-id,humidity
s1,10
s2,88
s1,NaN
```

leader-2.csv
```
sensor-id,humidity
s2,80
s3,NaN
s2,78
s1,98
```

### Expected Output Example

```
Num of processed files: 2
Num of processed measurements: 7
Num of failed measurements: 2

Sensors with highest avg humidity:

sensor-id,min,avg,max
s1,10,54,98
s2,78,82,88
s3,NaN,NaN,NaN
```
