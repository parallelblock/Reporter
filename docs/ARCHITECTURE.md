# Reporter architecture

This document assumes familiarity of the API the project provides, and its
purpose. It attempts to describe the seemingly bizarre architectural decisions
made when making it, and guidance for continual development.

This document ALSO assumes the reader has a familiarity of Java reflection,
the cross-plugin class cache functionality of Bukkit, and the reload semantics
of Bukkit.

Reporter is effectively a codebase with two completely bytecode isolated
projects, yet near native performance interoperability between them. The first
part, the central plugin itself, aggregates statistics from multiple unrelated
copies of the second part, the stub. An individual stub code instance is
completely isolated from another (think two plugins shading the stub dependency
to different eventual compiled packages), and each generated instance from said
code is once again unique from other generated code in operation created from
the same bytecode, minus any shared static caches (such as for reflection proxy
method lookups).

Reporter has the requirement that a stub be able to 'stub' itself out into a
no-op implementation if the central aggregation plugin (henceforth referred to simply
as the Reporter plugin) is not present, and be able to hot-load the Reporter
plugin in the case the running server plugin engine enables it, causing the
no-op implementation to instead delegate its calls in some manner to hot-created
Prometheus Java API objects. The Reporter plugin should be able to understand
hot-unloading of stub instances, and the subsequent reloading of said stub
instances on the fly.

In effect, we have the following simple requirements:

+ A given stub may be shaded into any legal classpath, either colliding with
  other stubs or being completely unique, with a completely non-deterministic
  package name (as this is decided by a reporting stub implementing codebase).
+ A given stub may not be the same version of compiled of the plugin loaded onto
  the server. As long as they are the same major version, they will have
  compatibility. This is consistent with the semantic versioning scheme.
+ A server instance may or may not have a backing Reporter plugin. If the server
  instance does not have a corresponding Reporter plugin enabled, the stubs will
  make a best-effort to provide a no-op implementation.
+ As the stub may not contain the Reporter plugin, the stub may not directly
  reference any class, method, or field within the Reporter plugin (as it may
  not exist in the runtime of the server instance).
+ A single runtime of a stub instance must be able to deal with different
  bytecode classes of the underlying Reporter plugin. This allows for the
  Reporter plugin to be reloaded by the Bukkit /reload method, or for the plugin
  to be unloaded and reloaded in a hot manner, whether to change versions, or
  whatever.
+ A single runtime of a Reporter plugin instance must be able to deal with
  multiple stub bytecode versions with no deterministic containing package. As
  long as the major versions of both the stub and Reporter plugin are the same,
  they are to have 100% compatibility unilaterally. This also enforces the fact
  that the Reporter plugin may not interface with any class, method, or field of
  a stub directly, only through reflection.

## Performance concerns

From reading the above, you can see the barrier between a stub instance and a
Reporter plugin instance is completely achieved through reflection, completely
throwing away any sort of JVM optimization on the matter. As performance in
Minecraft is ALWAYS a concern, especially on the main thread, various measures
are implemented to reduce the performance impact of a JVM reflection call.

Instead of using reflection, we instead use cached MethodHandles. MethodHandles
are far faster than Reflection, however are only available +JDK7. Seeing as
security holes are never a good thing, we should encourage our users to at least
use JDK7, thus allowing us to use this new, cool, and in general fast feature.
