# NOTE / WARNING / IMPORTANT:
This is NOT the right way to do this.
The RIGHT way would be to not use Dockerfile and use a real buildah build where we can
run oscap remediation against the mounted file system and then close up the file system
into an image.

But.....right now.....just trying to get a reference working....
