package com.lef.scanner;

import android.util.Log;

/**
 *	�������࣬����beacon�ķ��书�ʺͷ���Ƶ��
 * @author lief
 *
 */
public class BaseSettings {
	public AdvertisingInterval advertisingInterval;
	public TransmitPower transmitPower;
	protected BaseSettings() {
		// TODO Auto-generated constructor stub
		this.advertisingInterval = AdvertisingInterval.UNKNOWN;
		this.transmitPower = TransmitPower.UNKNOWN;
	}
	protected BaseSettings(int advertisingInterval,int transmitPower) {
		// TODO Auto-generated constructor stub
		this.advertisingInterval = getAdvertisingIntervalByInt(advertisingInterval);
		this.transmitPower = getTransmitPowerByInt(transmitPower);
	}
	/**
	 * �㲥Ƶ��<br>
	 * VERYHIGH(0),HIGH(1),MEDIUM(2),LOW(3),VERYLOW(4),MIN(5),UNKNOWN(6);<br>
	 * 0:100ms<br>
	 * 1:300ms<br>
	 * 2:500ms<br>
	 * 3:1s<br>
	 * 4:2s<br>
	 * 5:5s<br>
	 * @author lief
	 *
	 */
	public enum AdvertisingInterval {
		/**
		 * 10:100ms
		 */
		MAX(10),
		/**
		 * 30:300ms
		 */
		HIGH(30),
		/**
		 * 50:500ms
		 */
		NORMAL(50),
		/**
		 * 75:750ms
		 */
		LOW(75),
		/**
		 * 200:2s
		 */
		MIN(200),
		UNKNOWN(-1);
		int aValue;
		private AdvertisingInterval(int aValue){
			this.aValue = aValue;
		}
		@Override
        public String toString() {
            return String.valueOf(this.aValue);
        }
		
	}
	/**
	 *
	 * 0:-30dbm<br> 2m
	 * 1:-20dbm<br> 7m
	 * 2:-16dbm<br> 10m
	 * 3:-12dbm<br> 15m
	 * 4:-8dbm<br>  22m
	 * 5:-4dbm<br>  27m
	 * 6:-0dbm<br>  50m
	 * 7:4dbm<br>   90m
	 * @author lief
	 *
	 */
	public enum TransmitPower{
		MAX(4),		//4
		VERYHIGH(0),//0
		HIGH(-4),	//14
		MEDIUM(-8),	//18
		LOW(-12),		//22
		VERYLOW(-16),	//26
		MIN(-20),		//30
		UNKNOWN(-1);
		int aValue;
		private TransmitPower(int aValue){
			this.aValue = aValue;
		}
		@Override
        public String toString() {
            return String.valueOf(this.aValue);
        }
	}
	/**
	 * ��ȡ�����ã������߲���Ҫʹ�ã�������ó�ѡ��ʽ�ķ�ʽ
	 * @param aValue
	 * @return
	 */
	public static AdvertisingInterval getAdvertisingIntervalByInt(int aValue){		
		AdvertisingInterval[] ads = AdvertisingInterval.values();
		for(AdvertisingInterval ad :ads){
			if(ad.aValue == aValue){
				return ad;
			}
		}
		return AdvertisingInterval.UNKNOWN;
	}
	/**
	 * ��ȡ�����ã������߲���Ҫʹ�ã�������ó�ѡ��ʽ�ķ�ʽ
	 * @param aValue
	 * @return
	 */
	public static TransmitPower getTransmitPowerByInt(int aValue){		
		TransmitPower[] ads = TransmitPower.values();
		for(TransmitPower ad :ads){
			if(ad.aValue == aValue){
				return ad;
			}
		}
		return TransmitPower.UNKNOWN;
	}
	/**
	 * ��ȡbeacon�㲥Ƶ��
	 * @return
	 */
	public  AdvertisingInterval getAdvertisingInterval() {
		return advertisingInterval;
	}
	/**
	 * ��ȡbeacon���书��
	 * @return
	 */
	public TransmitPower getTransmitPower() {
		return transmitPower;
	}
	protected void setaInterval(AdvertisingInterval aInterval) {
		this.advertisingInterval = aInterval;
	}
	protected void settPower(TransmitPower tPower) {
		this.transmitPower = tPower;
	}
}