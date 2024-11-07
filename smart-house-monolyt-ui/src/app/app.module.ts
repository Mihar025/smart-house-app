import { NgModule } from "@angular/core";
import { AppComponent } from "./app.component";
import { BrowserModule } from "@angular/platform-browser";
import { HTTP_INTERCEPTORS, HttpClientModule, provideHttpClient, withFetch } from "@angular/common/http";
import { AppRoutingModule } from "./app-routing.module";
import { FormsModule } from "@angular/forms";
import { CommonModule } from "@angular/common";
import { CodeInputModule } from "angular-code-input";
import { LoginComponent } from "./pages/login/login.component";
import { RegisterComponent } from "./pages/register/register.component";
import { HttpTokenInterceptor } from "./services/interceptor/http-token.interceptor";
import { JWT_OPTIONS, JwtHelperService } from "@auth0/angular-jwt";
import { ActivateAccComponent } from "./pages/activate-acc/activate-acc.component";
import {MenuComponent} from "./modules/main-page/components/menu/menu.component";
import {MainPageComponent} from "./modules/main-page/pages/main-page/main-page.component";
import {RouterModule} from "@angular/router";
import {routes} from "./app.routes";

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    RegisterComponent,
    ActivateAccComponent,
    MenuComponent,
    MainPageComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    CommonModule,
    CodeInputModule
  ],
  providers: [
    provideHttpClient(withFetch()),
    {
      provide: HTTP_INTERCEPTORS,
      useClass: HttpTokenInterceptor,
      multi: true
    },
    {provide: JWT_OPTIONS, useValue: JWT_OPTIONS},
    JwtHelperService
  ],
  exports: [
    MenuComponent
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
