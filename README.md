App Engine Website
====================

[![Build Status](https://travis-ci.org/Todd-Davies/appengine-website.svg?branch=master)](https://travis-ci.org/Todd-Davies/appengine-website)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/7a52610faecb48b199ec433e5a97b31e)](https://www.codacy.com/app/todd434/appengine-website?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=Todd-Davies/appengine-website&amp;utm_campaign=Badge_Grade)
[![Dependency Status](https://www.versioneye.com/user/projects/57a484051dadcb004d68172f/badge.svg?style=flat-square)](https://www.versioneye.com/user/projects/57a484051dadcb004d68172f)

The source code for [my website](http://todddavies.co.uk), running on AppEngine with a Java stack (Guice, Guava, Maven, JUnit, etc).

Running locally
----------------

1. `git clone git@github.com:Todd-Davies/appengine-website.git`
2. `cd appengine-website`
3. `mvn appengine:devserver`
4. Navigate to `http://localhost:8000`

Other juicy bits
------------------

Run the tests with `mvn verify`.

Deploying is done with `mvn appengine:update`.