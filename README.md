# Shopify → Bling ERP · Live Demo

Backend em Spring Boot + Frontend interativo para portfólio.

---

## Deploy em 3 passos

### 1. Backend → Railway

1. Acesse [railway.app](https://railway.app) e crie conta
2. **New Project → Deploy from GitHub Repo** → selecione este repositório
3. O Railway detecta o `nixpacks.toml` e builda com Maven automaticamente
4. Copie a URL pública gerada (ex: `https://seu-app.up.railway.app`)

**Variáveis de ambiente opcionais:**
```
BLING_FAILURE_RATE=0.3    # % de falhas simuladas (padrão: 30%)
```

### 2. Frontend → Netlify

1. Acesse [netlify.com](https://netlify.com) → **Add new site → Import existing project**
2. Selecione o repositório
3. Build command: (deixe em branco) | Publish directory: `frontend`
4. Deploy

### 3. Conectar frontend ao backend

1. Abra a URL do Netlify
2. Clique em **⚙️ Configurar API**
3. Informe a URL do Railway
4. Status muda para **Backend online** 🟢

---

## Tech Stack

| Camada | Tecnologia |
|---|---|
| Framework | Spring Boot 3.2 |
| Linguagem | Java 17 |
| Retry | Spring Retry (@Retryable + @Recover) |
| Banco | H2 in-memory |
| ORM | Spring Data JPA |
| Build | Maven |
| Deploy backend | Railway |
| Deploy frontend | Netlify |

---

## Endpoints

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/webhook/orders` | Recebe pedido do Shopify |
| GET | `/logs` | Lista todos os logs |
| GET | `/logs/summary` | Totais: sucesso / falha |
| GET | `/logs/order/{id}` | Logs de um pedido específico |

MIT License
