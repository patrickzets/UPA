import { Routes } from '@angular/router';
import { PainelComponent } from './paginas/painel.component';
import { PacientesComponent } from './paginas/pacientes.component';
import { AgendaComponent } from './paginas/agenda.component';
import { HistoricoComponent } from './paginas/historico.component';
import { ConfiguracoesComponent } from './paginas/configuracoes.component';

export const routes: Routes = [
  { path: '', component: PainelComponent },
  { path: 'pacientes', component: PacientesComponent },
  { path: 'agenda', component: AgendaComponent },
  { path: 'historico', component: HistoricoComponent },
  { path: 'configuracoes', component: ConfiguracoesComponent }
];
