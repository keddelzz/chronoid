# Chronoid
Chronoid is a small tool, which was created for capturing a timelapse of my desktop for [Ludum Dare](http://ludumdare.com/compo/). As of now the programm is a command line application.

---

## Content

- [Content](#content)
- [Building](#building)
- [Usage](#usage)

---

## Building

This project uses [sbt](http://www.scala-sbt.org/) for building.

|  Command         | Effect                                                   |
|------------------|----------------------------------------------------------|
| `sbt run`        | Compile and run the project.                             |
| `sbt packageBin` | Compile the project and assemble an executable jar file. |
| `sbt test`       | Compile and run the unit tests.                          |

## Usage

```
syntax: chronoid <filename>.<extension> <interval> <target>
where
 -e | --extension  File extention for output file
 -i | --interval   Interval between two screenshots
 -f | --filename   Suffix of the name, which is used for screenshots
 -t | --target     Target directory

 Options can be given in any order.
```
