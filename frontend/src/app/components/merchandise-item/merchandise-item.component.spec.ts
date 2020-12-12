import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MerchandiseItemComponent } from './merchandise-item.component';

describe('MerchandiseItemComponent', () => {
  let component: MerchandiseItemComponent;
  let fixture: ComponentFixture<MerchandiseItemComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MerchandiseItemComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MerchandiseItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
