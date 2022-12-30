import { NgModule } from '@angular/core'
import { BrowserModule } from '@angular/platform-browser'

import { AppRoutingModule } from './app-routing.module'
import { AppComponent } from './app.component'
import { NgbModule } from '@ng-bootstrap/ng-bootstrap'
import { NavbarComponent } from './components/navbar/navbar.component'
import { ClientsComponent } from './components/clients/clients.component'
import { RoomsComponent } from './components/rooms/rooms.component'
import { EmployeesComponent } from './components/employees/employees.component'
import { PagenotfoundComponent } from './components/pagenotfound/pagenotfound.component'
import { HomepageComponent } from './components/homepage/homepage.component'
import { HttpClientModule } from '@angular/common/http'
import { LoginComponent } from './components/login/login.component'
import { ReactiveFormsModule } from '@angular/forms'

@NgModule({
	declarations: [
		AppComponent,
		NavbarComponent,
		ClientsComponent,
		RoomsComponent,
		EmployeesComponent,
		PagenotfoundComponent,
		HomepageComponent,
		LoginComponent,
	],
	imports: [BrowserModule, AppRoutingModule, NgbModule, HttpClientModule, ReactiveFormsModule],
	providers: [],
	bootstrap: [AppComponent],
})
export class AppModule {}
