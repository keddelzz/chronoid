# Chronoid

Chronoid is a small tool, for capturing a series of screenshots for timelapses.  
As of now the programm is a command line application.

This is a reimplementation of Chronoid in [Scala](http://www.scala-lang.org/). 
Its first version was used during [Ludum Dare](http://ludumdare.com/compo/) 32.
Timelapses, which were created using the first implementation and [ffmpeg](https://ffmpeg.org/), 
can be found [here](https://www.youtube.com/playlist?list=PL0AsE1_PyxNFTE7ABEH2vosiW4yMXz3IO).

## Content

- [Content](#content)
- [Building](#building)
- [Usage](#usage)

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
