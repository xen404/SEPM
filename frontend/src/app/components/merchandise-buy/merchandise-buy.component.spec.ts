import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MerchandiseBuyComponent } from './merchandise-buy.component';

describe('MerchandiseBuyComponent', () => {
  let component: MerchandiseBuyComponent;
  let fixture: ComponentFixture<MerchandiseBuyComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MerchandiseBuyComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MerchandiseBuyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
