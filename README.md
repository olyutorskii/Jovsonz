# Jovsonz #

[![Java CI with Maven](https://github.com/olyutorskii/Jovsonz/actions/workflows/maven.yml/badge.svg)](https://github.com/olyutorskii/Jovsonz/actions/workflows/maven.yml)
[![CodeQL](https://github.com/olyutorskii/Jovsonz/actions/workflows/codeql-analysis.yml/badge.svg)](https://github.com/olyutorskii/Jovsonz/actions/workflows/codeql-analysis.yml)
-----------------------------------------------------------------------


## What is Jovsonz ? ##

* **Jovsonz** is a Java library
that supports [JSON][JSON] format text I/O.

* Jovsonz is a simple.
7 classes derived from JSON basic type, 2 Exception classes, for a total of 15 classes.

* Jovsonz OSS-project was hosted by [OSDN][OSDN](formerly known as SourceForge.jp)
until 2023 October.
We decided to switch to hosting on GitHub due to concerns about OSDN availability.


## Why did you make it ? ##

* JSON input/output code was added to chat game client [Jindolf][JINDOLF]
because no MIT-licensed JSON library for Java could be found as of 2009.
This code was later separated and became the Jovsonz library.


## API document ##
* [API docs](https://olyutorskii.github.io/Jovsonz/apidocs/index.html)
* [Coding Samples](https://github.com/olyutorskii/Jovsonz/wiki/Coding-Samples)
* [UML diagram](https://github.com/olyutorskii/Jovsonz/wiki/Class-diagram)
* [Maven report](https://olyutorskii.github.io/Jovsonz/)


## How to build ##

* Jovsonz needs to use [Maven 3.3.9+](https://maven.apache.org/)
and JDK 1.8+ to be built.

* Jovsonz runtime does not depend on any other library at all.
Just compile Java sources under `src/main/java/`
if you don't use Maven nor JUnit nor resource-access.


## License ##

* Code is under [The MIT License][MIT].


## Project founder ##

* By [Olyutorskii](https://github.com/olyutorskii) at 2009


## Key technology ##

- [JSON (Wikipedia)](https://en.wikipedia.org/wiki/JSON)
- [RFC4627](http://www.ietf.org/rfc/rfc4627.txt) (obsoleted)
- [RFC7159](http://www.ietf.org/rfc/rfc7159.txt) (obsoleted)
- [RFC8259](http://www.ietf.org/rfc/rfc8259.txt)


[JSON]: https://www.json.org/
[OSDN]: https://ja.osdn.net/projects/jovsonz/
[JINDOLF]: https://github.com/olyutorskii/Jindolf
[MIT]: https://opensource.org/licenses/MIT


--- EOF ---
