import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ActivateAccComponent } from './activate-acc.component';

describe('ActivateAccComponent', () => {
  let component: ActivateAccComponent;
  let fixture: ComponentFixture<ActivateAccComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ActivateAccComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ActivateAccComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
