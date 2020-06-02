"""
WSGI config for room_displayer_project project.

It exposes the WSGI callable as a module-level variable named ``application``.

For more information on this file, see
https://docs.djangoproject.com/en/3.0/howto/deployment/wsgi/
"""
KEYS_TO_LOAD = [
    'sn',
    'mail',
]
import os

from django.core.wsgi import get_wsgi_application

os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'room_displayer_project.settings')

django_app = get_wsgi_application()


def loading_app(wsgi_environ, start_response):
    global real_app
    import os
    for key in KEYS_TO_LOAD:
        try:
            os.environ[key] = wsgi_environ[key]
        except KeyError:
            # The WSGI environment doesn't have the key
            pass
    real_app = django_app

    return real_app(wsgi_environ, start_response)

real_app = loading_app

application = lambda env, start: real_app(env, start)


