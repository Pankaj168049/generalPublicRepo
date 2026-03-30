**Title:** Retrospective Change – ASM Service Restart Due to Heap Space Issue

**Change Type:** Retrospective / Emergency Fix

**Description:**

A heap space issue was observed in the ASM logs, leading to increased memory consumption and degraded performance. To mitigate the immediate impact and restore service stability, the ASM service was restarted.

This behavior is a known issue in **Axiomatics Policy Server 6.2**. Previously, this was discussed with **Axiomatics** via a service ticket. The vendor confirmed that prolonged or frequent usage of ASM 6.2 may result in increased memory usage and potential performance degradation.

It has been confirmed that this issue is resolved in **Axiomatics Policy Server 7**, but persists in version 6.2.

**Workaround Provided by Vendor:**

1. Upgrade to ASM 7
2. Increase JVM heap size and schedule periodic restarts (e.g., weekly)

**Decision & Action Plan:**

Since regularly restarting ASM is not considered a best practice for long-term stability, the decision has been made to upgrade from ASM 6.2 to ASM 7. The upgrade activity is currently in progress.

**Implementation Details:**

* ASM service was restarted to temporarily resolve the issue
* No configuration changes were made during this activity

**Impact:**

* Temporary service interruption during restart
* No data loss observed

**Risk Assessment:**

* Low (restart activity only)

**Rollback Plan:**

* Not applicable (service restart only)

**Future Plan:**

* Complete upgrade to ASM 7 to permanently resolve the issue
* Monitor memory utilization post-upgrade

**Reason for Retrospective Change:**
Immediate action (service restart) was required to restore system stability due to memory exhaustion, hence the change is being logged retrospectively for tracking and audit purposes.


-------------------------------------------------------------------------

**Change Type:** Emergency / Retrospective
**Category:** Application / Infrastructure
**Configuration Item (CI):** ASM Service – Axiomatics Policy Server 6.2
**Assignment Group:** (Your Team Name)
**Requested By:** (Your Name)
**Implemented By:** (Your Name)
**Environment:** Production

---

**Short Description:**
ASM service restarted due to heap space issue (known issue in ASM 6.2)

---

**Description:**
A heap space issue was identified in ASM logs, causing increased memory consumption and performance degradation. Immediate action was required to restore service stability; hence, the ASM service was restarted.

This is a known issue in Axiomatics Policy Server 6.2. As confirmed by Axiomatics via prior support discussions, long-running instances of ASM 6.2 may experience increased memory usage and slow performance.

The issue has been resolved in Axiomatics Policy Server 7, but remains in version 6.2.

---

**Business Justification:**
Service degradation due to memory exhaustion required immediate remediation to ensure system availability and avoid further impact.

---

**Implementation Plan:**

* Identified heap space issue from ASM logs
* Restarted ASM service to clear memory and restore normal operation
* Verified service health post restart

---

**Impact:**

* Brief service interruption during restart
* No data loss observed

---

**Risk & Mitigation:**

* **Risk:** Temporary downtime during restart
* **Mitigation:** Restart performed during low-usage window and service validated post-restart

---

**Rollback Plan:**
Not applicable (service restart activity only)

---

**Workaround / Vendor Recommendation:**
As per Axiomatics:

1. Upgrade to ASM 7
2. Increase heap size and schedule periodic restarts

---

**Decision:**
Periodic restarts are not a sustainable long-term solution. Therefore, the team has decided to proceed with upgrading to ASM 7.

---

**Future Plan:**

* Upgrade ASM 6.2 to ASM 7 (in progress)
* Monitor memory utilization post-upgrade

---

**Reason for Retrospective:**
Immediate action was required to resolve production impact; hence, the change is documented retrospectively for tracking and audit purposes.

----------------------------------------------------------------------------------

**Short Description:**
Retrospective: ASM restart due to heap space issue (known issue in ASM 6.2)

**Summary:**
ASM service experienced heap space issue causing performance degradation. Service was restarted to restore stability.

This is a known issue in Axiomatics Policy Server 6.2, confirmed by vendor. Issue is resolved in ASM 7.

**Action Taken:**

* ASM service restarted
* Service validated post restart

**Impact:**

* Temporary downtime
* No data loss

**Next Steps:**
Upgrade to ASM 7 (in progress) as permanent fix.

**Reason for Retrospective:**
Immediate action required to restore production service.
