DROP DATABASE IF EXISTS paymeback;
CREATE database paymeback;

\c paymeback

CREATE TABLE expense_group (
    group_id UUID PRIMARY KEY,
    group_name VARCHAR(255) NOT NULL,
    group_link_token VARCHAR(255) UNIQUE NOT NULL,
    group_default_currency VARCHAR(3) NOT NULL,
    group_created_ts TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    group_last_activity_ts TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    group_expiry_ts TIMESTAMPTZ NOT NULL
);

CREATE TABLE member(
    member_id UUID PRIMARY KEY,
    group_id UUID NOT NULL,
    member_name VARCHAR(255) NOT NULL,
    member_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    member_created_ts TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    member_removed_ts TIMESTAMPTZ,
    FOREIGN KEY (group_id) REFERENCES expense_group (group_id) ON DELETE CASCADE
);

CREATE TABLE expense(
    expense_id UUID PRIMARY KEY,
    group_id UUID NOT NULL,
    expense_owner_id UUID NOT NULL,
    expense_name VARCHAR(255) NOT NULL,
    expense_total_cost NUMERIC(10,2) NOT NULL,
    expense_currency VARCHAR(3) NOT NULL,
    expense_date DATE NOT NULL,
    expense_created_ts TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_archived BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (group_id) REFERENCES expense_group(group_id) ON DELETE CASCADE,
    FOREIGN KEY (expense_owner_id) REFERENCES member(member_id)
);

CREATE TABLE expense_participant(
    expense_id UUID NOT NULL,
    member_id UUID NOT NULL,
    amount_owed NUMERIC(10,2) NOT NULL,
    PRIMARY KEY (expense_id, member_id),
    FOREIGN KEY (expense_id) REFERENCES expense(expense_id) ON DELETE CASCADE,
    FOREIGN KEY (member_id) REFERENCES member(member_id)
);

CREATE TABLE settlement(
    settlement_id UUID PRIMARY KEY,
    group_id UUID NOT NULL,
    payer_member_id UUID NOT NULL,
    payee_member_id UUID NOT NULL,
    amount_paid NUMERIC(10,2) NOT NULL CHECK (amount_paid > 0),
    currency VARCHAR(3) NOT NULL,
    settlement_ts TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (group_id) REFERENCES expense_group(group_id) ON DELETE CASCADE,
    FOREIGN KEY (payee_member_id) REFERENCES member(member_id),
    FOREIGN KEY (payer_member_id) REFERENCES member(member_id)
);

CREATE TABLE audit_log(
    log_id UUID PRIMARY KEY,
    group_id UUID NOT NULL,
    actor_member_id UUID NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    log_message VARCHAR(255) NOT NULL,
    log_ts TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (group_id) REFERENCES expense_group(group_id) ON DELETE CASCADE,
    FOREIGN KEY (actor_member_id) REFERENCES member(member_id)
);

CREATE INDEX idx_members_group_id ON member(group_id);
CREATE INDEX idx_expenses_group_id ON expense(group_id);
CREATE INDEX idx_expenses_owner_id ON expense(expense_owner_id);
CREATE INDEX idx_expense_participants_member_id ON expense_participant(member_id);
CREATE INDEX idx_settlements_group_id ON settlement(group_id);
CREATE INDEX idx_audit_logs_group_id ON audit_log(group_id);
CREATE INDEX idx_groups_link_token ON expense_group(group_link_token);
CREATE INDEX idx_groups_expiry_ts ON expense_group(group_expiry_ts);
