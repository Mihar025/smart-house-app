import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpHeaders,
  HttpResponse
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { TokenService } from '../token/token.service';
import { tap } from 'rxjs/operators';

@Injectable()
export class HttpTokenInterceptor implements HttpInterceptor {
  constructor(private tokenService: TokenService) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    console.log('Intercepting request:', request.url);

    // Список эндпоинтов, не требующих токена
    const noTokenRequired = ['auth/register', 'auth/authenticate', 'activate-account'];

    if (noTokenRequired.some(endpoint => request.url.includes(endpoint))) {
      console.log('Skipping token for auth request');
      return next.handle(request);
    }

    const token = this.tokenService.token;
    if (token) {
      console.log('Adding token to request');
      const authReq = request.clone({
        headers: new HttpHeaders({
          'Authorization': `Bearer ${token}`
        })
      });
      return next.handle(authReq).pipe(
        tap(
          event => {
            if (event instanceof HttpResponse) {
              console.log('Response:', event);
            }
          },
          error => console.error('Error in interceptor:', error)
        )
      );
    }

    console.log('No token available, proceeding with original request');
    return next.handle(request).pipe(
      tap(
        event => {
          if (event instanceof HttpResponse) {
            console.log('Response:', event);
          }
        },
        error => console.error('Error in interceptor:', error)
      )
    );
  }
}
