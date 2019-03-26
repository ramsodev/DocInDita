package net.ramso.docindita.db.metadata;

public interface IBaseMetadata extends BasicMetadata {
	String getDiagram();
	boolean isScaleDiagram();
	String getDDL();
}
