/* tslint:disable */
/* eslint-disable */
/* Code generated by ng-openapi-gen DO NOT EDIT. */

import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';

import { SmartOutletRequest } from '../../models/smart-outlet-request';
import { SmartOutletResponse } from '../../models/smart-outlet-response';

export interface AddSmartOutlet$Params {
      body: SmartOutletRequest
}

export function addSmartOutlet(http: HttpClient, rootUrl: string, params: AddSmartOutlet$Params, context?: HttpContext): Observable<StrictHttpResponse<SmartOutletResponse>> {
  const rb = new RequestBuilder(rootUrl, addSmartOutlet.PATH, 'post');
  if (params) {
    rb.body(params.body, 'application/json');
  }

  return http.request(
    rb.build({ responseType: 'json', accept: 'application/json', context })
  ).pipe(
    filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
    map((r: HttpResponse<any>) => {
      return r as StrictHttpResponse<SmartOutletResponse>;
    })
  );
}

addSmartOutlet.PATH = '/outlet';
