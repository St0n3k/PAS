import { Injectable } from '@angular/core'
import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http'
import { catchError, Observable, switchMap, throwError } from 'rxjs'
import { environment } from '../../environments/environment'
import { AuthService } from '../services/auth.service'
import { Router } from '@angular/router'

@Injectable()
export class JwtInterceptor implements HttpInterceptor {
	constructor(private authService: AuthService, private router: Router) {}

	intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
		const token = localStorage.getItem('jwt')

		if (!request.url.startsWith(environment.apiUrl) || !token) {
			return next.handle(request).pipe(
				catchError((err) => {
					if (err.status === 403) {
						this.logout()
					}
					return throwError(() => err)
				}),
			)
		}

		const cloned = request.clone({
			setHeaders: {
				Authorization: `Bearer ${token}`,
			},
		})
		return next.handle(cloned)
	}

	logout() {
		this.authService.clearUserData()
		this.authService.authenticated.next(false)
		this.router.navigate(['/login'], { queryParams: { 'session-expired': true } })
	}
}
