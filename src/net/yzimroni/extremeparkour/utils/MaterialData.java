package net.yzimroni.extremeparkour.utils;

import org.bukkit.Material;

public class MaterialData {

	private Material material;
	private byte data;

	public MaterialData(Material material) {
		this(material, (byte) 0);
	}

	public MaterialData(Material material, byte data) {
		super();
		this.material = material;
		this.data = data;
	}

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public byte getData() {
		return data;
	}

	public void setData(byte data) {
		this.data = data;
	}

}
