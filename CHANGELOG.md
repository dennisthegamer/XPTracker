## [1.1.1] - 2026-07-19

### Changed
- Internal: the Java package was renamed from `com.example.xptracker` to
  `de.dennisthegamer.xptracker`, so both 26.x branches share one package layout.
  No user-visible change; the version is raised so the existing 1.1.0 release
  keeps the jars it was published with.

## [1.1.0] - 2026-07-14
- **Unified version** — Same release number across all supported Minecraft version branches (26.1 and 26.2) so every build carries one version.
- **Standardized jar naming** — Release jars now follow `xptracker-fabric-<version>+mc<range>` (e.g. `xptracker-fabric-1.1.0+mc26.2.jar`).
- **Corrected metadata** — Fixed author and contact information (Modrinth and GitHub links).
- **Modrinth description** — Added `MODRINTH.md` at the repository root.

## [1.0.1] - 2026-06-21
- **Support for Minecraft 26.2** — Updated mappings and dependencies for the latest Minecraft version. No functional changes.
- **YACL** Change config library from cloth-config to yet another config lib.
