# Building jraplmeter

This code is a rather crude state at the moment (I'm currently using a wrapper around jrapl, called jraplmeter) and the separate online clustering code in a single project right now, i.e,. it needs to be modularized which I'll do later.

To build, you must first build jrapl, which is included in /vendor.

```cd /vendor/jrapl-port```
```make```

After, you can build a jraplmeter.jar in the top level.

```ant jar```

