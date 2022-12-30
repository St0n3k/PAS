import { Component, OnInit } from '@angular/core'
import { AuthService } from '../../services/auth.service'

@Component({
	selector: 'app-navbar',
	templateUrl: './navbar.component.html',
	styleUrls: ['./navbar.component.css'],
})
export class NavbarComponent implements OnInit {
	isMenuCollapsed = true
	authenticated = false
	role = ''
	constructor(public authService: AuthService) {}

	ngOnInit(): void {
		this.authService.authenticated.subscribe((change) => {
			this.authenticated = change
			if (this.authenticated) {
				this.role = localStorage.getItem('role')!
			}
		})
	}

	onLogout() {
		this.authService.logout()
	}
}
