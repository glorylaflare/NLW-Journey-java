# 🛫 Projeto NLW Journey - Planner

Projeto de back-end desenvolvido para o evento NLW Journey de um planner de viagens utilizando Java 17 e Spring Boot. No projeto o usuário poderá consultar através de endpoints informações sobre a viagem, atividades durante a viagem e os seus respectivos participantes.

No projeto foi utilizado o Spring Web, Flyway, Spring Dev Tools, Lombok, Spring JPA e o H2 Database.

### Adições extras:

- [ ] Adicionar uma validação nos campos de data
    - [ ] A data de começo da viagem, é inferior a data de término viagem
    - [ ] A data de uma atividade está entre as datas da viagem
        
        **Exemplo:**
        Viagem entre os dias 20 á 25 de julho no Rio de Janeiro

        ⇒ Atividade 19 de julho     
        ⇒ Atividade 21 de julho
        
- [ ] Extração do core das trips pra dentro de uma classe Service
- [ ] Mapeamento das exceções da nossa aplicação
    - [ ] Com tratativas de erro personalizadas
  