# Android screenshot patch

Based on commit `a6174f0f0a474b6a7346445503e5a026cc71bb07` of this repository.

Android screenshots allocate their crop/scale output directly, draw on the View
thread, then encode and recycle on a worker. A process-wide single-flight gate
bounds retained bitmaps; concurrent captures return null. Output over four
million pixels and invalid dimensions return null before allocation. The
existing logical-pixel API, navigation, composition mode and iOS code are unchanged.

Validation in the consuming Flutter app: Android compilation and JVM tests for
output allocation size, crop bounds, invalid inputs and overflow rejection.
Physical-device screenshot/lifecycle testing remains required before rollout.
